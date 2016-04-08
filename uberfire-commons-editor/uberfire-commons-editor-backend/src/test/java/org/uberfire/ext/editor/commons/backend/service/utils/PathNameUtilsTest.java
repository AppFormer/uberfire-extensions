/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.editor.commons.backend.service.utils;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.java.nio.file.Path;
import org.uberfire.mocks.FileSystemTestingUtils;

import static org.junit.Assert.*;

public class PathNameUtilsTest {

    private static FileSystemTestingUtils fileSystemTestingUtils = new FileSystemTestingUtils();

    @Before
    public void setup() throws IOException {
        fileSystemTestingUtils.setup();
    }

    @After
    public void cleanupFileSystem() {
        fileSystemTestingUtils.cleanup();
    }

    @Test
    public void targetFolderPathTest() {
        assertEquals( "newFolderName", targetFolderName( "originalFolderName", "newFolderName" ) );
        assertEquals( "newFolderName", targetFolderName( "original.folder.name", "newFolderName" ) );
        assertEquals( "newFolderName", targetFolderName( "originalFolder.name", "newFolderName" ) );
        assertEquals( "new.folder.name", targetFolderName( "originalFolderName", "new.folder.name" ) );
        assertEquals( "new.folder.name", targetFolderName( "original.folder.name", "new.folder.name" ) );
        assertEquals( "new.folder.name", targetFolderName( "originalFolder.name", "new.folder.name" ) );
        assertEquals( "newFolder.name", targetFolderName( "originalFolderName", "newFolder.name" ) );
        assertEquals( "newFolder.name", targetFolderName( "original.folder.name", "newFolder.name" ) );
        assertEquals( "newFolder.name", targetFolderName( "originalFolder.name", "newFolder.name" ) );
    }

    @Test
    public void targetFilePathTest() {
        assertEquals( "newFileName", targetFileName( "originalFileName", "newFileName" ) );
        assertEquals( "newFileName.extension1.extension2", targetFileName( "originalFileName.extension1.extension2", "newFileName" ) );
        assertEquals( "newFileName.extension", targetFileName( "originalFileName.extension", "newFileName" ) );
        assertEquals( "newFileName.extension1.extension2", targetFileName( "originalFileName", "newFileName.extension1.extension2" ) );
        assertEquals( "newFileName.extension1.extension2.extension1.extension2", targetFileName( "originalFileName.extension1.extension2", "newFileName.extension1.extension2" ) );
        assertEquals( "newFileName.extension1.extension2.extension", targetFileName( "originalFileName.extension", "newFileName.extension1.extension2" ) );
        assertEquals( "newFileName.extension", targetFileName( "originalFileName", "newFileName.extension" ) );
        assertEquals( "newFileName.extension.extension1.extension2", targetFileName( "originalFileName.extension1.extension2", "newFileName.extension" ) );
        assertEquals( "newFileName.extension.extension", targetFileName( "originalFileName.extension", "newFileName.extension" ) );
    }

    private String targetFolderName( final String originalFolderName,
                                     final String newFolderName ) {
        final Path path = Paths.convert( PathFactory.newPath( "file", "git://amend-repo-test/" + originalFolderName + "/file" ) );
        fileSystemTestingUtils.getIoService().write( path, "content" );
        return Paths.convert( PathNameUtils.buildTargetPath( path.getParent(), newFolderName ) ).getFileName();
    }

    private String targetFileName( final String originalFileName,
                                   final String newFileName ) {
        final Path path = Paths.convert( PathFactory.newPath( originalFileName, "git://amend-repo-test/" + originalFileName ) );
        fileSystemTestingUtils.getIoService().write( path, "content" );
        return Paths.convert( PathNameUtils.buildTargetPath( path, newFileName ) ).getFileName();
    }
}
