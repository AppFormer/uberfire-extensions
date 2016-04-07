/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.metadata.backend.lucene.index;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uberfire.commons.lifecycle.PriorityDisposableRegistry;
import org.uberfire.ext.metadata.backend.lucene.LuceneConfig;
import org.uberfire.ext.metadata.backend.lucene.LuceneConfigBuilder;
import org.uberfire.ext.metadata.backend.lucene.fields.FieldFactory;
import org.uberfire.ext.metadata.engine.Indexer;
import org.uberfire.ext.metadata.engine.MetaModelStore;
import org.uberfire.ext.metadata.io.IOServiceIndexedImpl;
import org.uberfire.ext.metadata.io.IndexersFactory;
import org.uberfire.ext.metadata.io.KObjectUtil;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KObjectKey;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.FileAttribute;

public class LuceneIndexEngineTest {

    protected IOService ioService;
    private LuceneConfig config;
    private CountDownLatch indexerCountDownLatch;

    protected Path basePath;
    protected static final List<File> tempFiles = new ArrayList<File>();

    @Before
    public void setup() throws IOException {
        final String repositoryName = getRepositoryName();
        final String path = createTempDirectory().getAbsolutePath();
        System.setProperty("org.uberfire.nio.git.dir", path);
        System.setProperty("org.uberfire.nio.git.daemon.enabled", "false");
        System.setProperty("org.uberfire.nio.git.ssh.enabled", "false");
        System.setProperty("org.uberfire.sys.repo.monitor.disabled", "true");

        final URI newRepo = URI.create("git://" + repositoryName);

        try {
            IOService ioService = ioService();

            ioService.newFileSystem(newRepo, new HashMap<String, Object>());

            basePath = getDirectoryPath().resolveSibling("someNewOtherPath");
            ioService().write(basePath.resolve("dummy"), "<none>");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unable to complete setup: " + e.getMessage());
        }
    }

    @AfterClass
    @BeforeClass
    public static void cleanup() {
        for (final File tempFile : tempFiles) {
            FileUtils.deleteQuietly(tempFile);
        }
    }

    @Test
    public void testIoServiceClose() throws IOException, InterruptedException {

        String [] testFiles = new String [] { "one.txt", "two.txt" };

        for( int i = 0; i < testFiles.length; ++i ) {
            final Path path1 = basePath.resolve( testFiles[i] );
            final String drl1 = loadText( testFiles[i] );
            ioService().write( path1, drl1 );
        }

        // wait for indexing
        indexerCountDownLatch.await();

        // dispose of the IOService
        ioService().dispose();

        this.ioService = null;

        // Set to "true" to fix problem
        boolean undoLock = false;
        if( undoLock ) {
            config.dispose();
        }
        /**
         *  and reinitialize IOService..
         *
         * (BOOM! Lock still held -- unless config.dispose() has been called (see aove),
         *  unless       LuceneConfig.dispose() is called
         *  which calls  LuceneIndexManager.dispose()
         *  which calls  DirectoryLuceneIndex.dispose()
         *  which calls  DirectoryLuceneIndex.closeWriter()
         *  which calls  IndexWriter.close()
         *
         *  which frees up the lock... )
         */
        ioService();
    }

    public String getRepositoryName() {
        return this.getClass().getSimpleName();
    }

    protected static File createTempDirectory() throws IOException {
        final File temp = File.createTempFile("temp", Long.toString(System.nanoTime()));
        if (!(temp.delete())) {
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }
        if (!(temp.mkdir())) {
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        }
        tempFiles.add(temp);
        return temp;
    }

    protected int seed = new Random().nextInt(10000);

    protected Path getDirectoryPath() {
        final String repositoryName = getRepositoryName();
        final Path dir = ioService().get(URI.create("git://" + repositoryName + "/_someDir" + seed));
        ioService().deleteIfExists(dir);
        return dir;
    }

    protected IOService ioService() {
        if (ioService == null) {
            final Map<String, Analyzer> analyzers = getAnalyzers();
            LuceneConfigBuilder configBuilder = new LuceneConfigBuilder().withInMemoryMetaModelStore().usingAnalyzers(analyzers).useDirectoryBasedIndex().useInMemoryDirectory();

            configBuilder.useNIODirectory();
            if( config == null ) {
                config = configBuilder.build();
            }

            ioService = new IOServiceIndexedImpl(config.getIndexEngine());
            final CountDownTestIndexer indexer = new CountDownTestIndexer(ioService, 3);
            this.indexerCountDownLatch = indexer.getLatch();
            IndexersFactory.clear();
            IndexersFactory.addIndexer(indexer);
        }
        return ioService;
    }

    public Map<String, Analyzer> getAnalyzers() {
        return new HashMap<String, Analyzer>() {
            {
                put("one", new TestAnalyzer());
                put("two", new TestAnalyzer());
            }
        };
    }

    private final class TestAnalyzer extends Analyzer {

        @Override
        protected TokenStreamComponents createComponents(String fieldName) {
            // DBG Auto-generated method stub
            return null;
        }

    }

    public final class CountDownTestIndexer implements Indexer {

        private final IOService ioService;
        private final CountDownLatch countDownLatch;

        public CountDownTestIndexer(IOService ioServiceImpl, int countDown) {
            this.ioService = ioServiceImpl;
            this.countDownLatch = new CountDownLatch(countDown);
        }

        public CountDownLatch getLatch() {
            return countDownLatch;
        }

        @Override
        public boolean supportsPath(Path path) {
            return true;
        }

        @Override
        public KObject toKObject(Path path) {

            try {
                List<FileAttribute<?>> attrsList = new ArrayList<>();

                attrsList.add(new TestFileAttribute("name", path.getFileName().toString()));
                attrsList.add(new TestFileAttribute("path", path.toAbsolutePath().toString()));

                return KObjectUtil.toKObject(path, attrsList.toArray(new FileAttribute<?>[attrsList.size()]));
            } finally {
                countDownLatch.countDown();
            }
        }

        @Override
        public KObjectKey toKObjectKey(Path path) {
            return KObjectUtil.toKObjectKey(path);
        }

    }

    private final class TestFileAttribute implements FileAttribute<Object> {

        private final String name;
        private final Object value;

        public TestFileAttribute(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public Object value() {
            return value;
        }

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
