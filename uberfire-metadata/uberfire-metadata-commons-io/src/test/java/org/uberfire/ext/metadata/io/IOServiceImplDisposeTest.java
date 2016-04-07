package org.uberfire.ext.metadata.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Test;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.DefaultIndexBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.util.KObjectUtil;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.metadata.engine.Indexer;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KObjectKey;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

public class IOServiceImplDisposeTest extends BaseIndexTest {

    @Override
    protected String[] getRepositoryNames() {
        return new String [] { this.getClass().getSimpleName() };
    }

    @Override
    protected IOService ioService() {
        if( ioService == null ) {
            super.ioService();

            IndexersFactory.clear();
            IndexersFactory.addIndexer( new Indexer() {

                @Override
                public KObjectKey toKObjectKey( Path path ) {
                    KObject index = null;

                    try {

                        Set<Pair<String, String>> indexElements = indexElements = new HashSet<>();

                        index = KObjectUtil.toKObject(path, indexElements);
                    } catch( Exception e ) {
                        // Unexpected parsing or processing error
                        logger.error("Unable to index '" + path.toUri().toString() + "'.", e.getMessage(), e);
                    }

                    return index;
                    return null;
                }

                @Override
                public KObject toKObject( Path path ) {
                    // DBG Auto-generated method stub
                    return null;
                }

                @Override
                public boolean supportsPath( Path path ) {
                    // DBG Auto-generated method stub
                    return false;
                }
            };
        }
        return ioService;
    }

    @Test
    public void disposeAndRecreateIOServiceTest() throws Exception {
        IOService ioService = ioService();

        final String repositoryName = this.getClass().getSimpleName();
        int seed = new Random( 10L ).nextInt();
        final Path dir = ioService().get(URI.create("git://" + repositoryName + "/_someDir" + seed));
        ioService().deleteIfExists(dir);

        //Add test files
        String fileName = "file1.properties";
        final Path path1 = dir.resolve( fileName );
        final String drl1 = loadText( fileName );
        ioService().write( path1, drl1 );

        Thread.sleep(5000);

        // dispose of IOService
        ioService.dispose();

        // .. and recreate!
        ioService();
    }

    protected String loadText( final String fileName ) throws IOException {
        InputStream fileInputStream = this.getClass().getResourceAsStream( fileName );
        if( fileInputStream == null ) {
           File file = new File( fileName );
           if( file.exists() ) {
               fileInputStream = new FileInputStream(file);
           }
        }
        final BufferedReader br = new BufferedReader( new InputStreamReader( fileInputStream ) );
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while ( line != null ) {
                sb.append( line );
                sb.append( System.getProperty( "line.separator" ) );
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }
}
