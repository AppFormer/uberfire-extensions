package org.uberfire.ext.perspective.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.ext.perspective.editor.model.NewPerspectiveEditorEvent;
import org.uberfire.ext.perspective.editor.model.PerspectiveEditor;
import org.uberfire.ext.perspective.editor.model.PerspectiveEditorPersistenceAPI;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileVisitResult;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.SimpleFileVisitor;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;

import static org.uberfire.backend.server.util.Paths.*;
import static org.uberfire.commons.validation.PortablePreconditions.*;
import static org.uberfire.java.nio.file.Files.*;

@Service
@ApplicationScoped
public class PerspectiveEditorPersistence implements PerspectiveEditorPersistenceAPI {

    public static final String PERSPECTIVE_EDITOR = "perspective-editor";
    private Gson gson;

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    @Named("systemFS")
    private FileSystem fileSystem;

    @Inject
    private Event<NewPerspectiveEditorEvent> newPerspectiveEventEvent;

    @PostConstruct
    public void setup() {
        gsonFactory();
    }

    @Override
    public List<PerspectiveEditor> loadAll() {

        List<PerspectiveEditor> list = new ArrayList<PerspectiveEditor>();
        final Collection<String> perspectives = listPerspectives();
        for ( String perspective : perspectives ) {
            list.add( load( perspective ) );
        }
        return list;
    }

    @Override
    public Collection<String> listPerspectives() {
        final Collection<String> result = new ArrayList<String>();
        final Path perspectivesDir = fileSystem.getPath( PERSPECTIVE_EDITOR );

        if ( ioService.exists( perspectivesDir ) ) {
            walkFileTree( checkNotNull( "root", perspectivesDir ),
                          new SimpleFileVisitor<Path>() {
                              @Override
                              public FileVisitResult visitFile( final Path file,
                                                                final BasicFileAttributes attrs ) throws IOException {
                                  try {
                                      checkNotNull( "file", file );
                                      checkNotNull( "attrs", attrs );

                                      if ( attrs.isRegularFile() ) {
                                          final org.uberfire.backend.vfs.Path path = convert( file );
                                          result.add( file.getFileName().toString() );
                                      }
                                  } catch ( final Exception ex ) {
                                      return FileVisitResult.TERMINATE;
                                  }
                                  return FileVisitResult.CONTINUE;
                              }
                          } );
        }

        return result;
    }

    @Override
    public PerspectiveEditor load( String perspectiveName ) {
        Path perspectiveFile = resolvePerspectivePath( perspectiveName );
        String fileContent = ioService.readAllString( perspectiveFile );
        PerspectiveEditor perspectiveEditorJSON = gson.fromJson( fileContent, PerspectiveEditor.class );
        return perspectiveEditorJSON;
    }

    @Override
    public void save( PerspectiveEditor perspectiveContent ) {
        Path perspectiveFile = resolvePerspectivePath( perspectiveContent.getName() );
        String json = gson.toJson( perspectiveContent );
        ioService.write( perspectiveFile, json );
        newPerspectiveEventEvent.fire( new NewPerspectiveEditorEvent( perspectiveContent ) );
    }

    private Path resolvePerspectivePath( String perspectiveFile ) {
        Path perspectivePath = fileSystem.getPath( PERSPECTIVE_EDITOR );
        return perspectivePath.resolve( perspectiveFile );
    }

    void gsonFactory() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

    }
}
