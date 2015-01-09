package org.uberfire.ext.plugin.backend;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.ext.plugin.event.MediaAdded;
import org.uberfire.ext.plugin.model.Media;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.EncodingUtil;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.server.BaseUploadServlet;
import org.uberfire.server.MimeType;

public class PluginMediaServlet
        extends BaseUploadServlet {

    @Inject
    @Named("configIO")
    private IOService ioService;

    @Inject
    @Named("systemFS")
    private FileSystem fileSystem;

    @Inject
    private Event<MediaAdded> newMediaEvent;

    private String pattern = "/plugins/";

    private static MediaServletURI mediaServletURI = new MediaServletURI( "plugins/" );

    @Override
    public void init( final ServletConfig config ) throws ServletException {
        final String pattern = config.getInitParameter( "url-pattern" );
        if ( pattern != null && !pattern.trim().isEmpty() ) {
            if ( pattern.endsWith( "/" ) ) {
                this.pattern = pattern;
            } else {
                this.pattern = pattern + "/";
            }
            if ( this.pattern.startsWith( "/" ) ) {
                mediaServletURI.setURI( this.pattern.substring( 1 ) );
            } else {
                mediaServletURI.setURI( this.pattern );
            }

        }
    }

    @Produces
    @Named("MediaServletURI")
    public MediaServletURI produceMediaServletURI() {
        return mediaServletURI;
    }

    @Override
    public void doGet( final HttpServletRequest req,
                       final HttpServletResponse resp ) throws IOException {
        String mime = null;
        InputStream in;

        final String filename = EncodingUtil.decode( req.getRequestURI().substring( req.getContextPath().length() ) );
        final Path mediaPath = fileSystem.getPath( "plugins", filename.replace( pattern, "/" ) );
        if ( !ioService.exists( mediaPath ) ) {
            mime = "image/png";
            in = getClass().getResourceAsStream( "/nofound.png" );
        } else {
            mime = MimeType.fromExtension( "." + FilenameUtils.getExtension( mediaPath.getFileName().toString() ) ).getType();
            if ( mime != null && !mime.startsWith( "image/" ) ) {
                mime = "image/png";
                in = getClass().getResourceAsStream( "/placeholder.png" );
            } else {
                in = ioService.newInputStream( mediaPath );
            }
        }

        if ( mime == null ) {
            resp.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
            return;
        }

        resp.setContentType( mime );

        final OutputStream out = resp.getOutputStream();

        byte[] buf = new byte[ 1024 ];
        int count = 0;
        while ( ( count = in.read( buf ) ) >= 0 ) {
            out.write( buf, 0, count );
        }
        out.close();
        in.close();
    }

    @Override
    protected void doPost( HttpServletRequest req,
                           HttpServletResponse response ) throws ServletException, IOException {

        try {
            final String filename = req.getRequestURI().substring( req.getContextPath().length() );
            final String pluginName = filename.replace( pattern, "/" );
            if ( pluginName != null ) {
                final FileItem fileItem = getFileItem( req );
                final Path path = fileSystem.getPath( "plugins", pluginName, "media", fileItem.getName() );

                if ( ioService.exists( path ) ) {
                    writeResponse( response, "FAIL - ALREADY EXISTS" );
                    return;
                }

                writeFile( ioService, path, fileItem );

                newMediaEvent.fire( new MediaAdded( pluginName.substring( 1 ), new Media( pattern.substring( 1 ) + pluginName.substring( 1 ) + "/media/" + path.getFileName(), Paths.convert( path ) ) ) );

                writeResponse( response, "OK" );
            }
        } catch ( final Exception e ) {
            logError( e );
            writeResponse( response, "FAIL" );
        }
    }
}
