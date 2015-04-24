/*
 * Copyright 2015 JBoss Inc
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
package org.uberfire.ext.wires.core.grids.client.model.mergable;

import org.junit.Test;
import org.uberfire.ext.wires.core.grids.client.model.BaseGridCellValue;

import static org.junit.Assert.*;

public class GridMergingTest extends BaseGridTest {

    @Test
    public void testInitialSetup1() {
        final MergableGridData data = new MergableGridData();
        final MergableGridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                                   100 );
        final MergableGridColumn<String> gc2 = new MockMergableGridColumn<String>( "col2",
                                                                                   100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );

        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumns().size(); columnIndex++ ) {
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( "(" + columnIndex + ", " + rowIndex + ")" ) );
            }
        }

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            assertFalse( data.getRow( rowIndex ).isMerged() );
            for ( int columnIndex = 0; columnIndex < data.getColumns().size(); columnIndex++ ) {
                final MergableGridCell cell = data.getCell( rowIndex,
                                                            columnIndex );
                assertFalse( cell.isMerged() );
                assertEquals( 1,
                              cell.getMergedCellCount() );
            }
        }
    }

    @Test
    public void testInitialSetup2() {
        final MergableGridData data = new MergableGridData();
        final MergableGridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                                   100 );
        final MergableGridColumn<String> gc2 = new MockMergableGridColumn<String>( "col2",
                                                                                   100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );

        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumns().size(); columnIndex++ ) {
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( "(" + columnIndex + ", " + ( columnIndex == 0 ? "X" : rowIndex ) + ")" ) );
            }
        }

        assertGridIndexes( data,
                           new boolean[]{ true, true, true },
                           new boolean[]{ false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 3, "(0, X)" ), Expected.build( false, 1, "(1, 0)" ) },
                                   { Expected.build( true, 0, "(0, X)" ), Expected.build( false, 1, "(1, 1)" ) },
                                   { Expected.build( true, 0, "(0, X)" ), Expected.build( false, 1, "(1, 2)" ) },
                           } );
    }

    @Test
    public void testMergeNext1() {
        final MergableGridData data = new MergableGridData();
        final MergableGridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                                   100 );
        final MergableGridColumn<String> gc2 = new MockMergableGridColumn<String>( "col2",
                                                                                   100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );

        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumns().size(); columnIndex++ ) {
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( "(" + columnIndex + ", " + rowIndex + ")" ) );
            }
        }

        data.setCell( 1,
                      0,
                      new BaseGridCellValue<String>( "(0, 0)" ) );

        assertGridIndexes( data,
                           new boolean[]{ true, true, false },
                           new boolean[]{ false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 2, "(0, 0)" ), Expected.build( false, 1, "(1, 0)" ) },
                                   { Expected.build( true, 0, "(0, 0)" ), Expected.build( false, 1, "(1, 1)" ) },
                                   { Expected.build( false, 1, "(0, 2)" ), Expected.build( false, 1, "(1, 2)" ) },
                           } );
    }

    @Test
    public void testMergeNext2() {
        final MergableGridData data = new MergableGridData();
        final MergableGridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                                   100 );
        final MergableGridColumn<String> gc2 = new MockMergableGridColumn<String>( "col2",
                                                                                   100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );

        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumns().size(); columnIndex++ ) {
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( "(" + columnIndex + ", " + rowIndex + ")" ) );
            }
        }

        data.setCell( 2,
                      0,
                      new BaseGridCellValue<String>( "(0, 1)" ) );

        assertGridIndexes( data,
                           new boolean[]{ false, true, true },
                           new boolean[]{ false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, "(0, 0)" ), Expected.build( false, 1, "(1, 0)" ) },
                                   { Expected.build( true, 2, "(0, 1)" ), Expected.build( false, 1, "(1, 1)" ) },
                                   { Expected.build( true, 0, "(0, 1)" ), Expected.build( false, 1, "(1, 2)" ) },
                           } );
    }

    @Test
    public void testMergeNext3() {
        final MergableGridData data = new MergableGridData();
        final MergableGridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                                   100 );
        final MergableGridColumn<String> gc2 = new MockMergableGridColumn<String>( "col2",
                                                                                   100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );

        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumns().size(); columnIndex++ ) {
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( "(" + columnIndex + ", " + rowIndex + ")" ) );
            }
        }

        data.setCell( 1,
                      0,
                      new BaseGridCellValue<String>( "(0, 0)" ) );
        data.setCell( 2,
                      0,
                      new BaseGridCellValue<String>( "(0, 0)" ) );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true },
                           new boolean[]{ false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 3, "(0, 0)" ), Expected.build( false, 1, "(1, 0)" ) },
                                   { Expected.build( true, 0, "(0, 0)" ), Expected.build( false, 1, "(1, 1)" ) },
                                   { Expected.build( true, 0, "(0, 0)" ), Expected.build( false, 1, "(1, 2)" ) },
                           } );
    }

    @Test
    public void testMergePrevious1() {
        final MergableGridData data = new MergableGridData();
        final MergableGridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                                   100 );
        final MergableGridColumn<String> gc2 = new MockMergableGridColumn<String>( "col2",
                                                                                   100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );

        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumns().size(); columnIndex++ ) {
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( "(" + columnIndex + ", " + rowIndex + ")" ) );
            }
        }

        data.setCell( 0,
                      0,
                      new BaseGridCellValue<String>( "(0, 1)" ) );

        assertGridIndexes( data,
                           new boolean[]{ true, true, false },
                           new boolean[]{ false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 2, "(0, 1)" ), Expected.build( false, 1, "(1, 0)" ) },
                                   { Expected.build( true, 0, "(0, 1)" ), Expected.build( false, 1, "(1, 1)" ) },
                                   { Expected.build( false, 1, "(0, 2)" ), Expected.build( false, 1, "(1, 2)" ) },
                           } );
    }

    @Test
    public void testMergePrevious2() {
        final MergableGridData data = new MergableGridData();
        final MergableGridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                                   100 );
        final MergableGridColumn<String> gc2 = new MockMergableGridColumn<String>( "col2",
                                                                                   100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );

        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumns().size(); columnIndex++ ) {
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( "(" + columnIndex + ", " + rowIndex + ")" ) );
            }
        }

        data.setCell( 1,
                      0,
                      new BaseGridCellValue<String>( "(0, 2)" ) );

        assertGridIndexes( data,
                           new boolean[]{ false, true, true },
                           new boolean[]{ false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, "(0, 0)" ), Expected.build( false, 1, "(1, 0)" ) },
                                   { Expected.build( true, 2, "(0, 2)" ), Expected.build( false, 1, "(1, 1)" ) },
                                   { Expected.build( true, 0, "(0, 2)" ), Expected.build( false, 1, "(1, 2)" ) },
                           } );
    }

    @Test
    public void testMergePrevious3() {
        final MergableGridData data = new MergableGridData();
        final MergableGridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                                   100 );
        final MergableGridColumn<String> gc2 = new MockMergableGridColumn<String>( "col2",
                                                                                   100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );

        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumns().size(); columnIndex++ ) {
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( "(" + columnIndex + ", " + rowIndex + ")" ) );
            }
        }

        data.setCell( 1,
                      0,
                      new BaseGridCellValue<String>( "(0, 2)" ) );

        data.setCell( 0,
                      0,
                      new BaseGridCellValue<String>( "(0, 2)" ) );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true },
                           new boolean[]{ false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 3, "(0, 2)" ), Expected.build( false, 1, "(1, 0)" ) },
                                   { Expected.build( true, 0, "(0, 2)" ), Expected.build( false, 1, "(1, 1)" ) },
                                   { Expected.build( true, 0, "(0, 2)" ), Expected.build( false, 1, "(1, 2)" ) },
                           } );
    }

    @Test
    public void testMergeNonSequential() {
        final MergableGridData data = new MergableGridData();
        final MergableGridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                                   100 );
        final MergableGridColumn<String> gc2 = new MockMergableGridColumn<String>( "col2",
                                                                                   100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );

        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumns().size(); columnIndex++ ) {
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( "(" + columnIndex + ", " + rowIndex + ")" ) );
            }
        }

        data.setCell( 0,
                      0,
                      new BaseGridCellValue<String>( "(a, b)" ) );

        data.setCell( 2,
                      0,
                      new BaseGridCellValue<String>( "(a, b)" ) );

        data.setCell( 1,
                      0,
                      new BaseGridCellValue<String>( "(a, b)" ) );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true },
                           new boolean[]{ false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 3, "(a, b)" ), Expected.build( false, 1, "(1, 0)" ) },
                                   { Expected.build( true, 0, "(a, b)" ), Expected.build( false, 1, "(1, 1)" ) },
                                   { Expected.build( true, 0, "(a, b)" ), Expected.build( false, 1, "(1, 2)" ) },
                           } );
    }

    @Test
    public void testMergedUpdateCellValue() {
        final MergableGridData data = new MergableGridData();
        final MergableGridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                                   100 );
        final MergableGridColumn<String> gc2 = new MockMergableGridColumn<String>( "col2",
                                                                                   100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );

        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumns().size(); columnIndex++ ) {
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( "(" + columnIndex + ", " + rowIndex + ")" ) );
            }
        }

        data.setCell( 1,
                      0,
                      new BaseGridCellValue<String>( "(0, 0)" ) );

        assertGridIndexes( data,
                           new boolean[]{ true, true, false },
                           new boolean[]{ false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 2, "(0, 0)" ), Expected.build( false, 1, "(1, 0)" ) },
                                   { Expected.build( true, 0, "(0, 0)" ), Expected.build( false, 1, "(1, 1)" ) },
                                   { Expected.build( false, 1, "(0, 2)" ), Expected.build( false, 1, "(1, 2)" ) },
                           } );

        //Update cell value
        data.setCell( 0,
                      0,
                      new BaseGridCellValue<String>( "<changed>" ) );

        assertGridIndexes( data,
                           new boolean[]{ true, true, false },
                           new boolean[]{ false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 2, "<changed>" ), Expected.build( false, 1, "(1, 0)" ) },
                                   { Expected.build( true, 0, "<changed>" ), Expected.build( false, 1, "(1, 1)" ) },
                                   { Expected.build( false, 1, "(0, 2)" ), Expected.build( false, 1, "(1, 2)" ) },
                           } );
    }

    @Test
    public void testMergedMovedColumnUpdateCellValue1() {
        final MergableGridData data = new MergableGridData();
        final MergableGridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                                   100 );
        final MergableGridColumn<String> gc2 = new MockMergableGridColumn<String>( "col2",
                                                                                   100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );

        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumns().size(); columnIndex++ ) {
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( "(" + columnIndex + ", " + rowIndex + ")" ) );
            }
        }

        data.setCell( 1,
                      0,
                      new BaseGridCellValue<String>( "(0, 0)" ) );

        assertGridIndexes( data,
                           new boolean[]{ true, true, false },
                           new boolean[]{ false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 2, "(0, 0)" ), Expected.build( false, 1, "(1, 0)" ) },
                                   { Expected.build( true, 0, "(0, 0)" ), Expected.build( false, 1, "(1, 1)" ) },
                                   { Expected.build( false, 1, "(0, 2)" ), Expected.build( false, 1, "(1, 2)" ) },
                           } );

        //Move column
        data.moveColumnTo( 1,
                           gc1 );

        //Update cell value
        data.setCell( 0,
                      1,
                      new BaseGridCellValue<String>( "<changed>" ) );

        assertGridIndexes( data,
                           new boolean[]{ true, true, false },
                           new boolean[]{ false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, "(1, 0)" ), Expected.build( true, 2, "<changed>" ) },
                                   { Expected.build( false, 1, "(1, 1)" ), Expected.build( true, 0, "<changed>" ) },
                                   { Expected.build( false, 1, "(1, 2)" ), Expected.build( false, 1, "(0, 2)" ) },
                           } );
    }

    @Test
    public void testMergedMovedColumnUpdateCellValue2() {
        final MergableGridData data = new MergableGridData();
        final MergableGridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                                   100 );
        final MergableGridColumn<String> gc2 = new MockMergableGridColumn<String>( "col2",
                                                                                   100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );

        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumns().size(); columnIndex++ ) {
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( "(" + columnIndex + ", " + rowIndex + ")" ) );
            }
        }

        data.setCell( 1,
                      0,
                      new BaseGridCellValue<String>( "(0, 0)" ) );

        assertGridIndexes( data,
                           new boolean[]{ true, true, false },
                           new boolean[]{ false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 2, "(0, 0)" ), Expected.build( false, 1, "(1, 0)" ) },
                                   { Expected.build( true, 0, "(0, 0)" ), Expected.build( false, 1, "(1, 1)" ) },
                                   { Expected.build( false, 1, "(0, 2)" ), Expected.build( false, 1, "(1, 2)" ) },
                           } );

        //Move column
        data.moveColumnTo( 1,
                           gc1 );

        //Update cell value
        data.setCell( 0,
                      1,
                      new BaseGridCellValue<String>( "<changed>" ) );

        assertGridIndexes( data,
                           new boolean[]{ true, true, false },
                           new boolean[]{ false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, "(1, 0)" ), Expected.build( true, 2, "<changed>" ) },
                                   { Expected.build( false, 1, "(1, 1)" ), Expected.build( true, 0, "<changed>" ) },
                                   { Expected.build( false, 1, "(1, 2)" ), Expected.build( false, 1, "(0, 2)" ) },
                           } );

        data.setMerged( false );

        assertGridIndexes( data,
                           new boolean[]{ false, false, false },
                           new boolean[]{ false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, "(1, 0)" ), Expected.build( false, 1, "<changed>" ) },
                                   { Expected.build( false, 1, "(1, 1)" ), Expected.build( false, 1, "<changed>" ) },
                                   { Expected.build( false, 1, "(1, 2)" ), Expected.build( false, 1, "(0, 2)" ) },
                           } );

        data.setMerged( true );

        assertGridIndexes( data,
                           new boolean[]{ true, true, false },
                           new boolean[]{ false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, "(1, 0)" ), Expected.build( true, 2, "<changed>" ) },
                                   { Expected.build( false, 1, "(1, 1)" ), Expected.build( true, 0, "<changed>" ) },
                                   { Expected.build( false, 1, "(1, 2)" ), Expected.build( false, 1, "(0, 2)" ) },
                           } );
    }

    @Test
    public void testFullIndexing1() {
        final MergableGridData data = new MergableGridData();
        final MergableGridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                                   100 );
        final MergableGridColumn<String> gc2 = new MockMergableGridColumn<String>( "col2",
                                                                                   100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );

        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumns().size(); columnIndex++ ) {
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( "(" + columnIndex + ", " + rowIndex + ")" ) );
            }
        }

        data.setCell( 1,
                      0,
                      new BaseGridCellValue<String>( "(0, 0)" ) );
        data.setCell( 2,
                      0,
                      new BaseGridCellValue<String>( "(0, 0)" ) );

        //Check initial indexing
        assertGridIndexes( data,
                           new boolean[]{ true, true, true },
                           new boolean[]{ false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 3, "(0, 0)" ), Expected.build( false, 1, "(1, 0)" ) },
                                   { Expected.build( true, 0, "(0, 0)" ), Expected.build( false, 1, "(1, 1)" ) },
                                   { Expected.build( true, 0, "(0, 0)" ), Expected.build( false, 1, "(1, 2)" ) },
                           } );

        //Clear merging
        data.setMerged( false );

        assertGridIndexes( data,
                           new boolean[]{ false, false, false },
                           new boolean[]{ false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, "(0, 0)" ), Expected.build( false, 1, "(1, 0)" ) },
                                   { Expected.build( false, 1, "(0, 0)" ), Expected.build( false, 1, "(1, 1)" ) },
                                   { Expected.build( false, 1, "(0, 0)" ), Expected.build( false, 1, "(1, 2)" ) },
                           } );

        //Set merging
        data.setMerged( true );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true },
                           new boolean[]{ false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 3, "(0, 0)" ), Expected.build( false, 1, "(1, 0)" ) },
                                   { Expected.build( true, 0, "(0, 0)" ), Expected.build( false, 1, "(1, 1)" ) },
                                   { Expected.build( true, 0, "(0, 0)" ), Expected.build( false, 1, "(1, 2)" ) },
                           } );
    }

    @Test
    public void testFullIndexing2() {
        final MergableGridData data = new MergableGridData();
        final MergableGridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                                   100 );
        final MergableGridColumn<String> gc2 = new MockMergableGridColumn<String>( "col2",
                                                                                   100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );

        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumns().size(); columnIndex++ ) {
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( "(" + columnIndex + ", " + rowIndex + ")" ) );
            }
        }

        data.setCell( 1,
                      0,
                      new BaseGridCellValue<String>( "(0, 0)" ) );
        data.setCell( 2,
                      0,
                      new BaseGridCellValue<String>( "(0, 0)" ) );

        //Check initial indexing
        assertGridIndexes( data,
                           new boolean[]{ true, true, true, false, false },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 3, "(0, 0)" ), Expected.build( false, 1, "(1, 0)" ) },
                                   { Expected.build( true, 0, "(0, 0)" ), Expected.build( false, 1, "(1, 1)" ) },
                                   { Expected.build( true, 0, "(0, 0)" ), Expected.build( false, 1, "(1, 2)" ) },
                                   { Expected.build( false, 1, "(0, 3)" ), Expected.build( false, 1, "(1, 3)" ) },
                                   { Expected.build( false, 1, "(0, 4)" ), Expected.build( false, 1, "(1, 4)" ) }
                           } );

        //Clear merging
        data.setMerged( false );

        assertGridIndexes( data,
                           new boolean[]{ false, false, false, false, false },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, "(0, 0)" ), Expected.build( false, 1, "(1, 0)" ) },
                                   { Expected.build( false, 1, "(0, 0)" ), Expected.build( false, 1, "(1, 1)" ) },
                                   { Expected.build( false, 1, "(0, 0)" ), Expected.build( false, 1, "(1, 2)" ) },
                                   { Expected.build( false, 1, "(0, 3)" ), Expected.build( false, 1, "(1, 3)" ) },
                                   { Expected.build( false, 1, "(0, 4)" ), Expected.build( false, 1, "(1, 4)" ) }
                           } );

        //Set merging
        data.setMerged( true );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, false, false },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 3, "(0, 0)" ), Expected.build( false, 1, "(1, 0)" ) },
                                   { Expected.build( true, 0, "(0, 0)" ), Expected.build( false, 1, "(1, 1)" ) },
                                   { Expected.build( true, 0, "(0, 0)" ), Expected.build( false, 1, "(1, 2)" ) },
                                   { Expected.build( false, 1, "(0, 3)" ), Expected.build( false, 1, "(1, 3)" ) },
                                   { Expected.build( false, 1, "(0, 4)" ), Expected.build( false, 1, "(1, 4)" ) }
                           } );
    }

    @Test
    public void testFullIndexing3() {
        final MergableGridData data = new MergableGridData();
        final MergableGridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                                   100 );
        final MergableGridColumn<String> gc2 = new MockMergableGridColumn<String>( "col2",
                                                                                   100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );

        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumns().size(); columnIndex++ ) {
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( "(" + columnIndex + ", " + rowIndex + ")" ) );
            }
        }

        data.setCell( 3,
                      0,
                      new BaseGridCellValue<String>( "(0, 2)" ) );
        data.setCell( 4,
                      0,
                      new BaseGridCellValue<String>( "(0, 2)" ) );

        //Check initial indexing
        assertGridIndexes( data,
                           new boolean[]{ false, false, true, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, "(0, 0)" ), Expected.build( false, 1, "(1, 0)" ) },
                                   { Expected.build( false, 1, "(0, 1)" ), Expected.build( false, 1, "(1, 1)" ) },
                                   { Expected.build( true, 3, "(0, 2)" ), Expected.build( false, 1, "(1, 2)" ) },
                                   { Expected.build( true, 0, "(0, 2)" ), Expected.build( false, 1, "(1, 3)" ) },
                                   { Expected.build( true, 0, "(0, 2)" ), Expected.build( false, 1, "(1, 4)" ) }
                           } );

        //Clear merging
        data.setMerged( false );

        assertGridIndexes( data,
                           new boolean[]{ false, false, false, false, false },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, "(0, 0)" ), Expected.build( false, 1, "(1, 0)" ) },
                                   { Expected.build( false, 1, "(0, 1)" ), Expected.build( false, 1, "(1, 1)" ) },
                                   { Expected.build( false, 1, "(0, 2)" ), Expected.build( false, 1, "(1, 2)" ) },
                                   { Expected.build( false, 1, "(0, 2)" ), Expected.build( false, 1, "(1, 3)" ) },
                                   { Expected.build( false, 1, "(0, 2)" ), Expected.build( false, 1, "(1, 4)" ) }
                           } );

        //Set merging
        data.setMerged( true );

        assertGridIndexes( data,
                           new boolean[]{ false, false, true, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, "(0, 0)" ), Expected.build( false, 1, "(1, 0)" ) },
                                   { Expected.build( false, 1, "(0, 1)" ), Expected.build( false, 1, "(1, 1)" ) },
                                   { Expected.build( true, 3, "(0, 2)" ), Expected.build( false, 1, "(1, 2)" ) },
                                   { Expected.build( true, 0, "(0, 2)" ), Expected.build( false, 1, "(1, 3)" ) },
                                   { Expected.build( true, 0, "(0, 2)" ), Expected.build( false, 1, "(1, 4)" ) }
                           } );
    }

    @Test
    public void testMergeString() {
        final MergableGridData data = new MergableGridData();
        final MergableGridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                                   100 );
        data.appendColumn( gc1 );

        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumns().size(); columnIndex++ ) {
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( "a" ) );
            }
        }

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true },
                           new boolean[]{ false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 4, "a" ) },
                                   { Expected.build( true, 0, "a" ) },
                                   { Expected.build( true, 0, "a" ) },
                                   { Expected.build( true, 0, "a" ) }
                           } );
    }

    @Test
    public void testMergeString2() {
        final MergableGridData data = new MergableGridData();
        final MergableGridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                                   100 );
        data.appendColumn( gc1 );

        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumns().size(); columnIndex++ ) {
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( rowIndex == 0 ? "b" : "a" ) );
            }
        }

        assertGridIndexes( data,
                           new boolean[]{ false, true, true, true },
                           new boolean[]{ false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, "b" ) },
                                   { Expected.build( true, 3, "a" ) },
                                   { Expected.build( true, 0, "a" ) },
                                   { Expected.build( true, 0, "a" ) }
                           } );
    }

    @Test
    public void testMergeString3() {
        final MergableGridData data = new MergableGridData();
        final MergableGridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                                   100 );
        data.appendColumn( gc1 );

        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumns().size(); columnIndex++ ) {
                final String value = ( rowIndex == 0 || rowIndex == 3 || rowIndex == 4 ? "a" : "b" );
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( value ) );
            }
        }

        assertGridIndexes( data,
                           new boolean[]{ false, true, true, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, "a" ) },
                                   { Expected.build( true, 2, "b" ) },
                                   { Expected.build( true, 0, "b" ) },
                                   { Expected.build( true, 2, "a" ) },
                                   { Expected.build( true, 0, "a" ) }
                           } );

        data.setMerged( false );

        assertGridIndexes( data,
                           new boolean[]{ false, false, false, false, false },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, "a" ) },
                                   { Expected.build( false, 1, "b" ) },
                                   { Expected.build( false, 1, "b" ) },
                                   { Expected.build( false, 1, "a" ) },
                                   { Expected.build( false, 1, "a" ) }
                           } );

        data.setMerged( true );

        assertGridIndexes( data,
                           new boolean[]{ false, true, true, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, "a" ) },
                                   { Expected.build( true, 2, "b" ) },
                                   { Expected.build( true, 0, "b" ) },
                                   { Expected.build( true, 2, "a" ) },
                                   { Expected.build( true, 0, "a" ) }
                           } );
    }

    @Test
    public void testMergeBoolean1() {
        final MergableGridData data = new MergableGridData();
        final MergableGridColumn<Boolean> gc1 = new MockMergableGridColumn<Boolean>( "col1",
                                                                                     100 );
        data.appendColumn( gc1 );

        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumns().size(); columnIndex++ ) {
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<Boolean>( false ) );
            }
        }

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true },
                           new boolean[]{ false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 4, false ) },
                                   { Expected.build( true, 0, false ) },
                                   { Expected.build( true, 0, false ) },
                                   { Expected.build( true, 0, false ) }
                           } );
    }

    @Test
    public void testMergeBoolean2() {
        final MergableGridData data = new MergableGridData();
        final MergableGridColumn<Boolean> gc1 = new MockMergableGridColumn<Boolean>( "col1",
                                                                                     100 );
        data.appendColumn( gc1 );

        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumns().size(); columnIndex++ ) {
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<Boolean>( rowIndex == 0 ) );
            }
        }

        assertGridIndexes( data,
                           new boolean[]{ false, true, true, true },
                           new boolean[]{ false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, true ) },
                                   { Expected.build( true, 3, false ) },
                                   { Expected.build( true, 0, false ) },
                                   { Expected.build( true, 0, false ) }
                           } );
    }

    @Test
    public void testMergeBoolean3() {
        final MergableGridData data = new MergableGridData();
        final MergableGridColumn<Boolean> gc1 = new MockMergableGridColumn<Boolean>( "col1",
                                                                                     100 );
        data.appendColumn( gc1 );

        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumns().size(); columnIndex++ ) {
                final boolean value = rowIndex == 0 || rowIndex == 3 || rowIndex == 4;
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<Boolean>( value ) );
            }
        }

        assertGridIndexes( data,
                           new boolean[]{ false, true, true, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, true ) },
                                   { Expected.build( true, 2, false ) },
                                   { Expected.build( true, 0, false ) },
                                   { Expected.build( true, 2, true ) },
                                   { Expected.build( true, 0, true ) }
                           } );

        data.setMerged( false );

        assertGridIndexes( data,
                           new boolean[]{ false, false, false, false, false },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, true ) },
                                   { Expected.build( false, 1, false ) },
                                   { Expected.build( false, 1, false ) },
                                   { Expected.build( false, 1, true ) },
                                   { Expected.build( false, 1, true ) }
                           } );

        data.setMerged( true );

        assertGridIndexes( data,
                           new boolean[]{ false, true, true, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, true ) },
                                   { Expected.build( true, 2, false ) },
                                   { Expected.build( true, 0, false ) },
                                   { Expected.build( true, 2, true ) },
                                   { Expected.build( true, 0, true ) }
                           } );
    }

    @Test
    public void testMergedDeleteCellValue1() {
        final MergableGridData data = new MergableGridData();
        final MergableGridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                                   100 );
        final MergableGridColumn<String> gc2 = new MockMergableGridColumn<String>( "col2",
                                                                                   100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );

        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumns().size(); columnIndex++ ) {
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( "(" + columnIndex + ", " + rowIndex + ")" ) );
            }
        }

        data.setCell( 1,
                      0,
                      new BaseGridCellValue<String>( "(0, 0)" ) );

        assertGridIndexes( data,
                           new boolean[]{ true, true, false },
                           new boolean[]{ false, false, false },
                           new Expected[][]{
                                   { Expected.build( true, 2, "(0, 0)" ), Expected.build( false, 1, "(1, 0)" ) },
                                   { Expected.build( true, 0, "(0, 0)" ), Expected.build( false, 1, "(1, 1)" ) },
                                   { Expected.build( false, 1, "(0, 2)" ), Expected.build( false, 1, "(1, 2)" ) },
                           } );

        //Update cell value
        data.deleteCell( 0,
                         0 );

        assertGridIndexes( data,
                           new boolean[]{ false, false, false },
                           new boolean[]{ false, false, false },
                           new Expected[][]{
                                   { Expected.build( null ), Expected.build( false, 1, "(1, 0)" ) },
                                   { Expected.build( null ), Expected.build( false, 1, "(1, 1)" ) },
                                   { Expected.build( false, 1, "(0, 2)" ), Expected.build( false, 1, "(1, 2)" ) },
                           } );
    }

    @Test
    public void testMergedDeleteCellValue2() {
        final MergableGridData data = new MergableGridData();
        final MergableGridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                                   100 );
        final MergableGridColumn<String> gc2 = new MockMergableGridColumn<String>( "col2",
                                                                                   100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );

        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            for ( int columnIndex = 0; columnIndex < data.getColumns().size(); columnIndex++ ) {
                data.setCell( rowIndex,
                              columnIndex,
                              new BaseGridCellValue<String>( "(" + columnIndex + ", " + rowIndex + ")" ) );
            }
        }

        assertGridIndexes( data,
                           new boolean[]{ false, false, false },
                           new boolean[]{ false, false, false },
                           new Expected[][]{
                                   { Expected.build( false, 1, "(0, 0)" ), Expected.build( false, 1, "(1, 0)" ) },
                                   { Expected.build( false, 1, "(0, 1)" ), Expected.build( false, 1, "(1, 1)" ) },
                                   { Expected.build( false, 1, "(0, 2)" ), Expected.build( false, 1, "(1, 2)" ) },
                           } );

        //Update cell value
        data.deleteCell( 0,
                         0 );

        assertGridIndexes( data,
                           new boolean[]{ false, false, false },
                           new boolean[]{ false, false, false },
                           new Expected[][]{
                                   { Expected.build( null ), Expected.build( false, 1, "(1, 0)" ) },
                                   { Expected.build( false, 1, "(0, 1)" ), Expected.build( false, 1, "(1, 1)" ) },
                                   { Expected.build( false, 1, "(0, 2)" ), Expected.build( false, 1, "(1, 2)" ) },
                           } );
    }

}
