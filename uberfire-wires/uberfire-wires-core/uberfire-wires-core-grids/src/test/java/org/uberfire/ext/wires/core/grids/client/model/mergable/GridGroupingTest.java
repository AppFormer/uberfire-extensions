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
import static org.uberfire.ext.wires.core.grids.client.model.mergable.BaseGridTest.Expected.*;

public class GridGroupingTest extends BaseGridTest {

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
            final MergableGridRow row = data.getRow( rowIndex );
            assertFalse( row.isMerged() );
            assertFalse( row.isCollapsed() );
            for ( int columnIndex = 0; columnIndex < data.getColumns().size(); columnIndex++ ) {
                final MergableGridCell cell = data.getCell( rowIndex,
                                                            columnIndex );
                assertFalse( cell.isMerged() );
            }
        }

        assertEquals( 3,
                      data.getRowCount() );
    }

    @Test
    public void testGroup1() {
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

        //Group cells
        data.collapseCell( 0,
                           0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, false },
                           new boolean[]{ false, true, false },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 2, "(0, 0)" ), build( false, 1, "(1, 0)" ) },
                                   { build( true, 0, "(0, 0)" ), build( false, 1, "(1, 1)" ) },
                                   { build( false, 1, "(0, 2)" ), build( false, 1, "(1, 2)" ) }
                           } );

        //Ungroup cells
        data.expandCell( 0,
                         0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, false },
                           new boolean[]{ false, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 2, "(0, 0)" ), build( false, 1, "(1, 0)" ) },
                                   { build( true, 0, "(0, 0)" ), build( false, 1, "(1, 1)" ) },
                                   { build( false, 1, "(0, 2)" ), build( false, 1, "(1, 2)" ) }
                           } );
    }

    @Test
    public void testGroupNotCombineWhenCellsValuesUpdatedAbove() {
        //Tests that cells with the same value do not combine into existing collapsed blocks
        //Test #1 - Update cells above the existing collapsed block
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

        // [ (0,0) (1,0) ]
        // [ (0,1) (1,1) ]
        // [ (0,2) (1,2) ]
        // [ (0,2) (1,3) ]
        // [ (0,4) (1,4) ]

        data.setCell( 3,
                      0,
                      new BaseGridCellValue<String>( "(0, 2)" ) );

        //Group cells
        data.collapseCell( 2,
                           0 );

        assertGridIndexes( data,
                           new boolean[]{ false, false, true, true, false },
                           new boolean[]{ false, false, false, true, false },
                           new BaseGridTest.Expected[][]{
                                   { build( false, 1, "(0, 0)" ), build( false, 1, "(1, 0)" ) },
                                   { build( false, 1, "(0, 1)" ), build( false, 1, "(1, 1)" ) },
                                   { build( true, 2, "(0, 2)" ), build( false, 1, "(1, 2)" ) },
                                   { build( true, 0, "(0, 2)" ), build( false, 1, "(1, 3)" ) },
                                   { build( false, 1, "(0, 4)" ), build( false, 1, "(1, 4)" ) }
                           } );

        //Set cell above existing block (should not affect existing block)
        data.setCell( 1,
                      0,
                      new BaseGridCellValue<String>( "(0, 2)" ) );

        assertGridIndexes( data,
                           new boolean[]{ false, false, true, true, false },
                           new boolean[]{ false, false, false, true, false },
                           new BaseGridTest.Expected[][]{
                                   { build( false, 1, "(0, 0)" ), build( false, 1, "(1, 0)" ) },
                                   { build( false, 1, "(0, 2)" ), build( false, 1, "(1, 1)" ) },
                                   { build( true, 2, "(0, 2)" ), build( false, 1, "(1, 2)" ) },
                                   { build( true, 0, "(0, 2)" ), build( false, 1, "(1, 3)" ) },
                                   { build( false, 1, "(0, 4)" ), build( false, 1, "(1, 4)" ) }
                           } );

        //Set cell above existing block (should create a new block)
        data.setCell( 0,
                      0,
                      new BaseGridCellValue<String>( "(0, 2)" ) );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, false },
                           new boolean[]{ false, false, false, true, false },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 2, "(0, 2)" ), build( false, 1, "(1, 0)" ) },
                                   { build( true, 0, "(0, 2)" ), build( false, 1, "(1, 1)" ) },
                                   { build( true, 2, "(0, 2)" ), build( false, 1, "(1, 2)" ) },
                                   { build( true, 0, "(0, 2)" ), build( false, 1, "(1, 3)" ) },
                                   { build( false, 1, "(0, 4)" ), build( false, 1, "(1, 4)" ) }
                           } );

        //Ungroup cell (should result in a single block spanning 4 rows)
        data.expandCell( 2,
                         0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, false },
                           new boolean[]{ false, false, false, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 4, "(0, 2)" ), build( false, 1, "(1, 0)" ) },
                                   { build( true, 0, "(0, 2)" ), build( false, 1, "(1, 1)" ) },
                                   { build( true, 0, "(0, 2)" ), build( false, 1, "(1, 2)" ) },
                                   { build( true, 0, "(0, 2)" ), build( false, 1, "(1, 3)" ) },
                                   { build( false, 1, "(0, 4)" ), build( false, 1, "(1, 4)" ) }
                           } );
    }

    @Test
    public void testGroupNotCombineWhenCellsValuesUpdatedBelow() {
        //Tests that cells with the same value do not combine into existing collapsed blocks
        //Test #2 - Update cells below the existing collapsed block
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

        // [ (0,0) (1,0) ]
        // [ (0,1) (1,1) ]
        // [ (0,1) (1,2) ]
        // [ (0,3) (1,3) ]
        // [ (0,4) (1,4) ]

        data.setCell( 2,
                      0,
                      new BaseGridCellValue<String>( "(0, 1)" ) );

        //Group cells
        data.collapseCell( 1,
                           0 );

        assertGridIndexes( data,
                           new boolean[]{ false, true, true, false, false },
                           new boolean[]{ false, false, true, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( false, 1, "(0, 0)" ), build( false, 1, "(1, 0)" ) },
                                   { build( true, 2, "(0, 1)" ), build( false, 1, "(1, 1)" ) },
                                   { build( true, 0, "(0, 1)" ), build( false, 1, "(1, 2)" ) },
                                   { build( false, 1, "(0, 3)" ), build( false, 1, "(1, 3)" ) },
                                   { build( false, 1, "(0, 4)" ), build( false, 1, "(1, 4)" ) }
                           } );

        //Set cell below existing block (should not affect existing block)
        data.setCell( 3,
                      0,
                      new BaseGridCellValue<String>( "(0, 1)" ) );

        assertGridIndexes( data,
                           new boolean[]{ false, true, true, false, false },
                           new boolean[]{ false, false, true, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( false, 1, "(0, 0)" ), build( false, 1, "(1, 0)" ) },
                                   { build( true, 2, "(0, 1)" ), build( false, 1, "(1, 1)" ) },
                                   { build( true, 0, "(0, 1)" ), build( false, 1, "(1, 2)" ) },
                                   { build( false, 1, "(0, 1)" ), build( false, 1, "(1, 3)" ) },
                                   { build( false, 1, "(0, 4)" ), build( false, 1, "(1, 4)" ) }
                           } );

        //Set cell below existing block (should create a new block)
        data.setCell( 4,
                      0,
                      new BaseGridCellValue<String>( "(0, 1)" ) );

        assertGridIndexes( data,
                           new boolean[]{ false, true, true, true, true },
                           new boolean[]{ false, false, true, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( false, 1, "(0, 0)" ), build( false, 1, "(1, 0)" ) },
                                   { build( true, 2, "(0, 1)" ), build( false, 1, "(1, 1)" ) },
                                   { build( true, 0, "(0, 1)" ), build( false, 1, "(1, 2)" ) },
                                   { build( true, 2, "(0, 1)" ), build( false, 1, "(1, 3)" ) },
                                   { build( true, 0, "(0, 1)" ), build( false, 1, "(1, 4)" ) }
                           } );

        //Ungroup cell (should result in a single block spanning 4 rows)
        data.expandCell( 1,
                         0 );

        assertGridIndexes( data,
                           new boolean[]{ false, true, true, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( false, 1, "(0, 0)" ), build( false, 1, "(1, 0)" ) },
                                   { build( true, 4, "(0, 1)" ), build( false, 1, "(1, 1)" ) },
                                   { build( true, 0, "(0, 1)" ), build( false, 1, "(1, 2)" ) },
                                   { build( true, 0, "(0, 1)" ), build( false, 1, "(1, 3)" ) },
                                   { build( true, 0, "(0, 1)" ), build( false, 1, "(1, 4)" ) }
                           } );
    }

    @Test
    public void testGroupOverlap1() {
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

        // [   g1  (1,0) ]
        // [   g1    g2  ]      [   g1  (1,0) ]
        // [   g1    g2  ] ---> [   g1    g2  ] ---> [   g1  (1,0) ]
        // [   g1    g2  ]      [   g1  (1,4) ]
        // [   g1  (1,4) ]

        data.setCell( 0,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );
        data.setCell( 1,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );
        data.setCell( 2,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );
        data.setCell( 3,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );
        data.setCell( 4,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );

        data.setCell( 1,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 2,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 3,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );

        //Group g2
        data.collapseCell( 1,
                           1 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, false, true, true, false },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 5, "g1" ), build( false, 1, "(1, 0)" ) },
                                   { build( true, 0, "g1" ), build( true, 3, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 4)" ) }
                           } );

        //Group g1
        data.collapseCell( 0,
                           0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, true, true, true, true },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 5, "g1" ), build( false, 1, "(1, 0)" ) },
                                   { build( true, 0, "g1" ), build( true, 3, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 4)" ) }
                           } );

        //Ungroup g1
        data.expandCell( 0,
                         0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, false, true, true, false },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 5, "g1" ), build( false, 1, "(1, 0)" ) },
                                   { build( true, 0, "g1" ), build( true, 3, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 4)" ) }
                           } );
    }

    @Test
    public void testGroupOverlap2() {
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

        // [ (0,0)   g2  ]
        // [ (0,1)   g2  ]      [ (0,0)   g2  ]      [ (0,0)   g2  ]      [ (0,0)   g2  ]
        // [   g1    g2  ] ---> [   g1  (1,3) ] ---> [   g1  (1,3) ] ---> [   g1  (1,3) ]
        // [   g1  (1,3) ]      [   g1  (1,4) ]                           [   g1  (1,4) ]
        // [   g1  (1,4) ]

        data.setCell( 2,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );
        data.setCell( 3,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );
        data.setCell( 4,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );

        data.setCell( 0,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 1,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 2,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );

        //Group g2 - should split g1
        data.collapseCell( 0,
                           1 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, true, true, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( false, 1, "(0, 0)" ), build( true, 3, "g2" ) },
                                   { build( false, 1, "(0, 1)" ), build( true, 0, "g2" ) },
                                   { build( false, 1, "g1" ), build( true, 0, "g2" ) },
                                   { build( true, 2, "g1" ), build( false, 1, "(1, 3)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 4)" ) }
                           } );

        //Group g1
        data.collapseCell( 3,
                           0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, true, true, false, true },
                           new BaseGridTest.Expected[][]{
                                   { build( false, 1, "(0, 0)" ), build( true, 3, "g2" ) },
                                   { build( false, 1, "(0, 1)" ), build( true, 0, "g2" ) },
                                   { build( false, 1, "g1" ), build( true, 0, "g2" ) },
                                   { build( true, 2, "g1" ), build( false, 1, "(1, 3)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 4)" ) }
                           } );

        //Ungroup g1
        data.expandCell( 3,
                         0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, true, true, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( false, 1, "(0, 0)" ), build( true, 3, "g2" ) },
                                   { build( false, 1, "(0, 1)" ), build( true, 0, "g2" ) },
                                   { build( false, 1, "g1" ), build( true, 0, "g2" ) },
                                   { build( true, 2, "g1" ), build( false, 1, "(1, 3)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 4)" ) }
                           } );
    }

    @Test
    public void testGroupOverlap3() {
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

        // [   g1  (1,0) ]
        // [   g1  (1,1) ]      [   g1  (1,0) ]                           [   g1  (1,0) ]
        // [   g1    g2  ] ---> [ (0,3)   g2  ] ---> [ (0,3)   g2  ] ---> [ (0,3)   g2  ]
        // [ (0,3)   g2  ]      [ (0,4)   g2  ]                           [ (0,4)   g2  ]
        // [ (0,4)   g2  ]

        data.setCell( 0,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );
        data.setCell( 1,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );
        data.setCell( 2,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );

        data.setCell( 2,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 3,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 4,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );

        //Group g1 - should split g2
        data.collapseCell( 0,
                           0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, true, true, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 3, "g1" ), build( false, 1, "(1, 0)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 1)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "g2" ) },
                                   { build( false, 1, "(0, 3)" ), build( true, 2, "g2" ) },
                                   { build( false, 1, "(0, 4)" ), build( true, 0, "g2" ) }
                           } );

        //Group g2
        data.collapseCell( 3,
                           1 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, true, true, false, true },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 3, "g1" ), build( false, 1, "(1, 0)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 1)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "g2" ) },
                                   { build( false, 1, "(0, 3)" ), build( true, 2, "g2" ) },
                                   { build( false, 1, "(0, 4)" ), build( true, 0, "g2" ) }
                           } );

        //Ungroup g2
        data.expandCell( 3,
                         1 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, true, true, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 3, "g1" ), build( false, 1, "(1, 0)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 1)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "g2" ) },
                                   { build( false, 1, "(0, 3)" ), build( true, 2, "g2" ) },
                                   { build( false, 1, "(0, 4)" ), build( true, 0, "g2" ) }
                           } );
    }

    @Test
    public void testGroupOverlap4() {
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

        // [  g1    g2 ]
        // [  g1    g2 ]
        // [ (0,2)  g2 ]
        // [ (0,3)  g2 ]
        // [ (0,4)  g2 ]

        data.setCell( 0,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );
        data.setCell( 1,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );

        data.setCell( 0,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 1,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 2,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 3,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 4,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );

        //Group g1
        data.collapseCell( 0,
                           0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, true, false, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 2, "g1" ), build( true, 5, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( false, 1, "(0, 2)" ), build( true, 0, "g2" ) },
                                   { build( false, 1, "(0, 3)" ), build( true, 0, "g2" ) },
                                   { build( false, 1, "(0, 4)" ), build( true, 0, "g2" ) }
                           } );

        //Ungroup g1
        data.expandCell( 0,
                         0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 2, "g1" ), build( true, 5, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( false, 1, "(0, 2)" ), build( true, 0, "g2" ) },
                                   { build( false, 1, "(0, 3)" ), build( true, 0, "g2" ) },
                                   { build( false, 1, "(0, 4)" ), build( true, 0, "g2" ) }
                           } );
    }

    @Test
    public void testGroupOverlap5() {
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

        // [ (0,0)  (1,0) ]
        // [  g1    (1,1) ]
        // [  g1      g2  ]
        // [ (0,3)    g2  ]
        // [ (0,4)    g2  ]

        data.setCell( 1,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );
        data.setCell( 2,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );

        data.setCell( 2,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 3,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 4,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );

        //Group g1 - should split g2
        data.collapseCell( 1,
                           0 );

        assertGridIndexes( data,
                           new boolean[]{ false, true, true, true, true },
                           new boolean[]{ false, false, true, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( false, 1, "(0, 0)" ), build( false, 1, "(1, 0)" ) },
                                   { build( true, 2, "g1" ), build( false, 1, "(1, 1)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "g2" ) },
                                   { build( false, 1, "(0, 3)" ), build( true, 2, "g2" ) },
                                   { build( false, 1, "(0, 4)" ), build( true, 0, "g2" ) }
                           } );

        //Ungroup g1
        data.expandCell( 1,
                         0 );

        assertGridIndexes( data,
                           new boolean[]{ false, true, true, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( false, 1, "(0, 0)" ), build( false, 1, "(1, 0)" ) },
                                   { build( true, 2, "g1" ), build( false, 1, "(1, 1)" ) },
                                   { build( true, 0, "g1" ), build( true, 3, "g2" ) },
                                   { build( false, 1, "(0, 3)" ), build( true, 0, "g2" ) },
                                   { build( false, 1, "(0, 4)" ), build( true, 0, "g2" ) }
                           } );
    }

    @Test
    public void testGroupOverlap6() {
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

        // [ (0,0)  (1,0) ]
        // [  g1      g2  ]
        // [  g1      g2  ]
        // [ (0,3)    g2  ]
        // [ (0,4)    g2  ]

        data.setCell( 1,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );
        data.setCell( 2,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );

        data.setCell( 1,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 2,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 3,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 4,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );

        //Group g1 - doesn't need to split g2 since it spans all of g1
        data.collapseCell( 1,
                           0 );

        assertGridIndexes( data,
                           new boolean[]{ false, true, true, true, true },
                           new boolean[]{ false, false, true, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( false, 1, "(0, 0)" ), build( false, 1, "(1, 0)" ) },
                                   { build( true, 2, "g1" ), build( true, 4, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( false, 1, "(0, 3)" ), build( true, 0, "g2" ) },
                                   { build( false, 1, "(0, 4)" ), build( true, 0, "g2" ) }
                           } );

        //Ungroup g1
        data.expandCell( 1,
                         0 );

        assertGridIndexes( data,
                           new boolean[]{ false, true, true, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( false, 1, "(0, 0)" ), build( false, 1, "(1, 0)" ) },
                                   { build( true, 2, "g1" ), build( true, 4, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( false, 1, "(0, 3)" ), build( true, 0, "g2" ) },
                                   { build( false, 1, "(0, 4)" ), build( true, 0, "g2" ) }
                           } );
    }

    @Test
    public void testGroupOverlap7() {
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

        // [   g1   (1,0) ]
        // [   g1     g2  ]      [   g1  (1,0) ]
        // [   g1     g2  ] ---> [ (0,3)   g2  ]
        // [ (0,3)    g2  ]      [ (0,4)   g2  ]
        // [ (0,4)    g2  ]

        data.setCell( 0,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );
        data.setCell( 1,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );
        data.setCell( 2,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );

        data.setCell( 1,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 2,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 3,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 4,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );

        //Group g1 - should split g2
        data.collapseCell( 0,
                           0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, true, true, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 3, "g1" ), build( false, 1, "(1, 0)" ) },
                                   { build( true, 0, "g1" ), build( true, 2, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( false, 1, "(0, 3)" ), build( true, 2, "g2" ) },
                                   { build( false, 1, "(0, 4)" ), build( true, 0, "g2" ) }
                           } );

        //Ungroup g1
        data.expandCell( 0,
                         0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 3, "g1" ), build( false, 1, "(1, 0)" ) },
                                   { build( true, 0, "g1" ), build( true, 4, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( false, 1, "(0, 3)" ), build( true, 0, "g2" ) },
                                   { build( false, 1, "(0, 4)" ), build( true, 0, "g2" ) }
                           } );
    }

    @Test
    public void testGroupOverlap8() {
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

        // [   g1    g2  ]
        // [   g1    g2  ]
        // [   g1    g2  ] ---> [   g1    g2  ]
        // [   g1    g2  ]
        // [   g1    g2  ]

        for ( int rowIndex = 0; rowIndex < data.getRowCount(); rowIndex++ ) {
            data.setCell( rowIndex,
                          0,
                          new BaseGridCellValue<String>( "g1" ) );
            data.setCell( rowIndex,
                          1,
                          new BaseGridCellValue<String>( "g2" ) );
        }

        //Group g1
        data.collapseCell( 0,
                           0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, true, true, true, true },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 5, "g1" ), build( true, 5, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) }
                           } );

        //Ungroup g1
        data.expandCell( 0,
                         0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 5, "g1" ), build( true, 5, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) }
                           } );
    }

    @Test
    public void testGroupOverlap9() {
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

        // [   g1    g2  ]
        // [   g1    g2  ]
        // [   g1    g2  ] ---> [   g1    g2  ]
        // [   g1    g2  ]      [ (0,4) (1,4) ]
        // [ (0,4) (1,4) ]

        for ( int rowIndex = 0; rowIndex < data.getRowCount() - 1; rowIndex++ ) {
            data.setCell( rowIndex,
                          0,
                          new BaseGridCellValue<String>( "g1" ) );
            data.setCell( rowIndex,
                          1,
                          new BaseGridCellValue<String>( "g2" ) );
        }

        data.setCell( 4,
                      0,
                      new BaseGridCellValue<String>( "(0, 4)" ) );
        data.setCell( 4,
                      1,
                      new BaseGridCellValue<String>( "(1, 4)" ) );

        //Group g1
        data.collapseCell( 0,
                           0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, false },
                           new boolean[]{ false, true, true, true, false },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 4, "g1" ), build( true, 4, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( false, 1, "(0, 4)" ), build( false, 1, "(1, 4)" ) }
                           } );

        //Ungroup g1
        data.expandCell( 0,
                         0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, false },
                           new boolean[]{ false, false, false, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 4, "g1" ), build( true, 4, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( false, 1, "(0, 4)" ), build( false, 1, "(1, 4)" ) }
                           } );
    }

    @Test
    public void testGroupOverlap10() {
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

        // [   g1  (1,0) ]
        // [   g1  (1,1) ]      [   g1  (1,0) ]      [   g1  (1,0) ]      [   g1  (1,0) ]      [   g1  (1,0) ]
        // [   g1    g2  ] ---> [ (0,3)   g2  ] ---> [ (0,3)   g2  ] ---> [   g1  (1,1) ] ---> [ (0,3)   g2  ]
        // [ (0,3)   g2  ]      [ (0,4)   g2  ]                           [   g1    g2  ]
        // [ (0,4)   g2  ]                                                [ (0,3)   g2  ]

        data.setCell( 0,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );
        data.setCell( 1,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );
        data.setCell( 2,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );

        data.setCell( 2,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 3,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 4,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );

        //Group g1 - should split g2
        data.collapseCell( 0,
                           0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, true, true, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 3, "g1" ), build( false, 1, "(1, 0)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 1)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "g2" ) },
                                   { build( false, 1, "(0, 3)" ), build( true, 2, "g2" ) },
                                   { build( false, 1, "(0, 4)" ), build( true, 0, "g2" ) }
                           } );

        //Group g2
        data.collapseCell( 3,
                           1 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, true, true, false, true },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 3, "g1" ), build( false, 1, "(1, 0)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 1)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "g2" ) },
                                   { build( false, 1, "(0, 3)" ), build( true, 2, "g2" ) },
                                   { build( false, 1, "(0, 4)" ), build( true, 0, "g2" ) }
                           } );

        //Ungroup g1 - should not recombine g2 as it has been split and collapsed
        data.expandCell( 0,
                         0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, false, false, false, true },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 3, "g1" ), build( false, 1, "(1, 0)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 1)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "g2" ) },
                                   { build( false, 1, "(0, 3)" ), build( true, 2, "g2" ) },
                                   { build( false, 1, "(0, 4)" ), build( true, 0, "g2" ) }
                           } );

        //Group g1 - check re-applying collapse preserves indexing
        data.collapseCell( 0,
                           0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, true, true, false, true },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 3, "g1" ), build( false, 1, "(1, 0)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 1)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "g2" ) },
                                   { build( false, 1, "(0, 3)" ), build( true, 2, "g2" ) },
                                   { build( false, 1, "(0, 4)" ), build( true, 0, "g2" ) }
                           } );

        //Ungroup g1 - check re-applying collapse preserves indexing
        data.expandCell( 0,
                         0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, false, false, false, true },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 3, "g1" ), build( false, 1, "(1, 0)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 1)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "g2" ) },
                                   { build( false, 1, "(0, 3)" ), build( true, 2, "g2" ) },
                                   { build( false, 1, "(0, 4)" ), build( true, 0, "g2" ) }
                           } );
    }

    @Test
    public void testGroupOverlap11() {
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

        // [   g1    g2  ]                           [   g1    g2  ]
        // [   g1    g2  ]                           [   g1    g2  ]
        // [   g1    g2  ] ---> [   g1    g2  ] ---> [   g1    g2  ]
        // [ (0,3)   g2  ]                           [ (0,3)   g2  ]
        // [ (0,4)   g2  ]                           [ (0,4)   g2  ]

        data.setCell( 0,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );
        data.setCell( 1,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );
        data.setCell( 2,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );

        data.setCell( 0,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 1,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 2,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 3,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 4,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );

        //Group g2
        data.collapseCell( 0,
                           1 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, true, true, true, true },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 3, "g1" ), build( true, 5, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( false, 1, "(0, 3)" ), build( true, 0, "g2" ) },
                                   { build( false, 1, "(0, 4)" ), build( true, 0, "g2" ) }
                           } );

        //Ungroup g1 - should result in g2 being split and collapsed
        data.expandCell( 0,
                         0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, false, false, false, true },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 3, "g1" ), build( true, 3, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( false, 1, "(0, 3)" ), build( true, 2, "g2" ) },
                                   { build( false, 1, "(0, 4)" ), build( true, 0, "g2" ) }
                           } );

        //Ungroup g2 - should restore to original configuration
        data.expandCell( 3,
                         1 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 3, "g1" ), build( true, 5, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( false, 1, "(0, 3)" ), build( true, 0, "g2" ) },
                                   { build( false, 1, "(0, 4)" ), build( true, 0, "g2" ) }
                           } );
    }

    @Test
    public void testGroupOverlap12() {
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

        // [   g1    g2  ]                           [   g1    g2  ]
        // [   g1    g2  ]      [   g1    g2  ]      [   g1    g2  ]
        // [   g1    g2  ] ---> [ (0,3)   g2  ] ---> [   g1    g2  ]
        // [ (0,3)   g2  ]      [ (0,4)   g2  ]      [ (0,3)   g2  ]
        // [ (0,4)   g2  ]                           [ (0,4)   g2  ]

        data.setCell( 0,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );
        data.setCell( 1,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );
        data.setCell( 2,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );

        data.setCell( 0,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 1,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 2,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 3,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 4,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );

        //Group g1
        data.collapseCell( 0,
                           0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, true, true, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 3, "g1" ), build( true, 5, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( false, 1, "(0, 3)" ), build( true, 0, "g2" ) },
                                   { build( false, 1, "(0, 4)" ), build( true, 0, "g2" ) }
                           } );

        //Ungroup g2
        data.expandCell( 0,
                         1 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 3, "g1" ), build( true, 5, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( false, 1, "(0, 3)" ), build( true, 0, "g2" ) },
                                   { build( false, 1, "(0, 4)" ), build( true, 0, "g2" ) }
                           } );

        //Group g2
        data.collapseCell( 0,
                           1 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, true, true, true, true },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 3, "g1" ), build( true, 5, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ) },
                                   { build( false, 1, "(0, 3)" ), build( true, 0, "g2" ) },
                                   { build( false, 1, "(0, 4)" ), build( true, 0, "g2" ) }
                           } );
    }

    @Test
    public void testGroupOverlap13() {
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

        // [ (0,0)   g2  ]
        // [ (0,1)   g2  ]      [ (0,0)   g2  ]
        // [   g1    g2  ] ---> [ (0,1)   g2  ] ---> [ (0,0)   g2  ]
        // [   g1  (1,3) ]      [   g1    g2  ]      [   g1    g2  ]
        // [   g1  (1,4) ]

        data.setCell( 2,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );
        data.setCell( 3,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );
        data.setCell( 4,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );

        data.setCell( 0,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 1,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 2,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );

        //Group g1 - should split g2
        data.collapseCell( 2,
                           0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, false, false, true, true },
                           new BaseGridTest.Expected[][]{
                                   { build( false, 1, "(0, 0)" ), build( true, 2, "g2" ) },
                                   { build( false, 1, "(0, 1)" ), build( true, 0, "g2" ) },
                                   { build( true, 3, "g1" ), build( false, 1, "g2" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 3)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 4)" ) }
                           } );

        //Group g2
        data.collapseCell( 0,
                           1 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, true, false, true, true },
                           new BaseGridTest.Expected[][]{
                                   { build( false, 1, "(0, 0)" ), build( true, 2, "g2" ) },
                                   { build( false, 1, "(0, 1)" ), build( true, 0, "g2" ) },
                                   { build( true, 3, "g1" ), build( false, 1, "g2" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 3)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 4)" ) }
                           } );

        //Ungroup g1 - g2 should remain split
        data.expandCell( 2,
                         0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, true, false, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( false, 1, "(0, 0)" ), build( true, 2, "g2" ) },
                                   { build( false, 1, "(0, 1)" ), build( true, 0, "g2" ) },
                                   { build( true, 3, "g1" ), build( false, 1, "g2" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 3)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 4)" ) }
                           } );

        //Ungroup g2 - g2 should not be split
        data.expandCell( 0,
                         1 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( false, 1, "(0, 0)" ), build( true, 3, "g2" ) },
                                   { build( false, 1, "(0, 1)" ), build( true, 0, "g2" ) },
                                   { build( true, 3, "g1" ), build( true, 0, "g2" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 3)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 4)" ) }
                           } );
    }

    @Test
    public void testGroupOverlap14() {
        final MergableGridData data = new MergableGridData();
        final MergableGridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                                   100 );
        final MergableGridColumn<String> gc2 = new MockMergableGridColumn<String>( "col2",
                                                                                   100 );
        final MergableGridColumn<String> gc3 = new MockMergableGridColumn<String>( "col3",
                                                                                   100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );
        data.appendColumn( gc3 );

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

        // [ (0,0) (1,0)   g3  ]
        // [ (0,1) (1,1)   g3  ]      [ (0,0) (1,0)   g3  ]
        // [   g1    g2    g3  ] ---> [   g1    g2  (2,3) ] ---> [ (0,0) (1,0)   g3  ]
        // [   g1    g2  (2,3) ]      [ (0,4)   g2  (2,4) ]      [   g1    g2  (2,3) ]
        // [ (0,4)   g2  (2,4) ]

        data.setCell( 2,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );
        data.setCell( 3,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );

        data.setCell( 2,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 3,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 4,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );

        data.setCell( 0,
                      2,
                      new BaseGridCellValue<String>( "g3" ) );
        data.setCell( 1,
                      2,
                      new BaseGridCellValue<String>( "g3" ) );
        data.setCell( 2,
                      2,
                      new BaseGridCellValue<String>( "g3" ) );

        //Check initial setup
        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, false, false, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( false, 1, "(0, 0)" ), build( false, 1, "(1, 0)" ), build( true, 3, "g3" ) },
                                   { build( false, 1, "(0, 1)" ), build( false, 1, "(1, 1)" ), build( true, 0, "g3" ) },
                                   { build( true, 2, "g1" ), build( true, 3, "g2" ), build( true, 0, "g3" ) },
                                   { build( true, 0, "g1" ), build( true, 0, "g2" ), build( false, 1, "(2, 3)" ) },
                                   { build( false, 1, "(0, 4)" ), build( true, 0, "g2" ), build( false, 1, "(2, 4)" ) }
                           } );

        //Group g3 - should split g1 and g2
        data.collapseCell( 0,
                           2 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, true, true, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( false, 1, "(0, 0)" ), build( false, 1, "(1, 0)" ), build( true, 3, "g3" ) },
                                   { build( false, 1, "(0, 1)" ), build( false, 1, "(1, 1)" ), build( true, 0, "g3" ) },
                                   { build( false, 1, "g1" ), build( false, 1, "g2" ), build( true, 0, "g3" ) },
                                   { build( false, 1, "g1" ), build( true, 2, "g2" ), build( false, 1, "(2, 3)" ) },
                                   { build( false, 1, "(0, 4)" ), build( true, 0, "g2" ), build( false, 1, "(2, 4)" ) }
                           } );

        //Group g2
        data.collapseCell( 3,
                           1 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true },
                           new boolean[]{ false, true, true, false, true },
                           new BaseGridTest.Expected[][]{
                                   { build( false, 1, "(0, 0)" ), build( false, 1, "(1, 0)" ), build( true, 3, "g3" ) },
                                   { build( false, 1, "(0, 1)" ), build( false, 1, "(1, 1)" ), build( true, 0, "g3" ) },
                                   { build( false, 1, "g1" ), build( false, 1, "g2" ), build( true, 0, "g3" ) },
                                   { build( false, 1, "g1" ), build( true, 2, "g2" ), build( false, 1, "(2, 3)" ) },
                                   { build( false, 1, "(0, 4)" ), build( true, 0, "g2" ), build( false, 1, "(2, 4)" ) }
                           } );
    }

    @Test
    public void testGroupOverlap15() {
        final MergableGridData data = new MergableGridData();
        final MergableGridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                                   100 );
        final MergableGridColumn<String> gc2 = new MockMergableGridColumn<String>( "col2",
                                                                                   100 );
        final MergableGridColumn<String> gc3 = new MockMergableGridColumn<String>( "col3",
                                                                                   100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );
        data.appendColumn( gc3 );

        data.appendRow( new MergableGridRow() );
        data.appendRow( new MergableGridRow() );
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

        // [ (0,0) (1,0)   g3  ]
        // [ (0,1) (1,1)   g3  ]      [ (0,0) (1,0)   g3  ]      [ (0,0) (1,0)   g3  ]
        // [ (0,2)   g2    g3) ]      [ (0,1) (1,1)   g3  ]      [ (0,1) (1,1)   g3  ]      [ (0,0) (1,0)   g3  ]
        // [ (0,3)   g2  (2,3) ] ---> [ (0,2)   g2    g3  ] ---> [ (0,2)   g2    g3  ] ---> [   g1    g2  (2,4) ]
        // [   g1    g2  (2,4) ]      [ (0,3)   g2  (2,3) ]      [   g1    g2  (2,4) ]
        // [   g1  (1,5) (2,5) ]      [   g1    g2  (2,4) ]
        // [   g1  (1,6) (2,6) ]

        data.setCell( 4,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );
        data.setCell( 5,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );
        data.setCell( 6,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );

        data.setCell( 2,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 3,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 4,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );

        data.setCell( 0,
                      2,
                      new BaseGridCellValue<String>( "g3" ) );
        data.setCell( 1,
                      2,
                      new BaseGridCellValue<String>( "g3" ) );
        data.setCell( 2,
                      2,
                      new BaseGridCellValue<String>( "g3" ) );

        //Check initial setup
        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true, true, true },
                           new boolean[]{ false, false, false, false, false, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( false, 1, "(0, 0)" ), build( false, 1, "(1, 0)" ), build( true, 3, "g3" ) },
                                   { build( false, 1, "(0, 1)" ), build( false, 1, "(1, 1)" ), build( true, 0, "g3" ) },
                                   { build( false, 1, "(0, 2)" ), build( true, 3, "g2" ), build( true, 0, "g3" ) },
                                   { build( false, 1, "(0, 3)" ), build( true, 0, "g2" ), build( false, 1, "(2, 3)" ) },
                                   { build( true, 3, "g1" ), build( true, 0, "g2" ), build( false, 1, "(2, 4)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 5)" ), build( false, 1, "(2, 5)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 6)" ), build( false, 1, "(2, 6)" ) }
                           } );

        //Group g1 - should split g2
        data.collapseCell( 4,
                           0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true, true, true },
                           new boolean[]{ false, false, false, false, false, true, true },
                           new BaseGridTest.Expected[][]{
                                   { build( false, 1, "(0, 0)" ), build( false, 1, "(1, 0)" ), build( true, 3, "g3" ) },
                                   { build( false, 1, "(0, 1)" ), build( false, 1, "(1, 1)" ), build( true, 0, "g3" ) },
                                   { build( false, 1, "(0, 2)" ), build( true, 2, "g2" ), build( true, 0, "g3" ) },
                                   { build( false, 1, "(0, 3)" ), build( true, 0, "g2" ), build( false, 1, "(2, 3)" ) },
                                   { build( true, 3, "g1" ), build( false, 1, "g2" ), build( false, 1, "(2, 4)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 5)" ), build( false, 1, "(2, 5)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 6)" ), build( false, 1, "(2, 6)" ) }
                           } );

        //Group g2 - should split g1
        data.collapseCell( 2,
                           1 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true, true, true },
                           new boolean[]{ false, false, false, true, false, true, true },
                           new BaseGridTest.Expected[][]{
                                   { build( false, 1, "(0, 0)" ), build( false, 1, "(1, 0)" ), build( true, 2, "g3" ) },
                                   { build( false, 1, "(0, 1)" ), build( false, 1, "(1, 1)" ), build( true, 0, "g3" ) },
                                   { build( false, 1, "(0, 2)" ), build( true, 2, "g2" ), build( false, 1, "g3" ) },
                                   { build( false, 1, "(0, 3)" ), build( true, 0, "g2" ), build( false, 1, "(2, 3)" ) },
                                   { build( true, 3, "g1" ), build( false, 1, "g2" ), build( false, 1, "(2, 4)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 5)" ), build( false, 1, "(2, 5)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 6)" ), build( false, 1, "(2, 6)" ) }
                           } );

        //Group g3
        data.collapseCell( 0,
                           2 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, true, true, true },
                           new boolean[]{ false, true, false, true, false, true, true },
                           new BaseGridTest.Expected[][]{
                                   { build( false, 1, "(0, 0)" ), build( false, 1, "(1, 0)" ), build( true, 2, "g3" ) },
                                   { build( false, 1, "(0, 1)" ), build( false, 1, "(1, 1)" ), build( true, 0, "g3" ) },
                                   { build( false, 1, "(0, 2)" ), build( true, 2, "g2" ), build( false, 1, "g3" ) },
                                   { build( false, 1, "(0, 3)" ), build( true, 0, "g2" ), build( false, 1, "(2, 3)" ) },
                                   { build( true, 3, "g1" ), build( false, 1, "g2" ), build( false, 1, "(2, 4)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 5)" ), build( false, 1, "(2, 5)" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 6)" ), build( false, 1, "(2, 6)" ) }
                           } );
    }

    @Test
    public void testGroupOverlap16() {
        final MergableGridData data = new MergableGridData();
        final MergableGridColumn<String> gc1 = new MockMergableGridColumn<String>( "col1",
                                                                                   100 );
        final MergableGridColumn<String> gc2 = new MockMergableGridColumn<String>( "col2",
                                                                                   100 );
        final MergableGridColumn<String> gc3 = new MockMergableGridColumn<String>( "col3",
                                                                                   100 );
        data.appendColumn( gc1 );
        data.appendColumn( gc2 );
        data.appendColumn( gc3 );

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

        // [ (0,0)   g2    g3  ]
        // [   g1    g2    g3  ]      [ (0,0)   g2    g3  ]      [ (0,0)   g2    g3  ]
        // [   g1  (1,2)   g3  ] ---> [   g1  (1,2)   g3  ] ---> [   g1  (1,3) (2,3) ]
        // [   g1  (1,3) (2,3) ]      [   g1  (1,3) (2,3) ]      [ (0,4) (1,4) (2,4) ]
        // [ (0,4) (1,4) (2,4) ]      [ (0,4) (1,4) (2,4) ]

        data.setCell( 1,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );
        data.setCell( 2,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );
        data.setCell( 3,
                      0,
                      new BaseGridCellValue<String>( "g1" ) );

        data.setCell( 0,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );
        data.setCell( 1,
                      1,
                      new BaseGridCellValue<String>( "g2" ) );

        data.setCell( 0,
                      2,
                      new BaseGridCellValue<String>( "g3" ) );
        data.setCell( 1,
                      2,
                      new BaseGridCellValue<String>( "g3" ) );
        data.setCell( 2,
                      2,
                      new BaseGridCellValue<String>( "g3" ) );

        //Check initial setup
        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, false },
                           new boolean[]{ false, false, false, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( false, 1, "(0, 0)" ), build( true, 2, "g2" ), build( true, 3, "g3" ) },
                                   { build( true, 3, "g1" ), build( true, 0, "g2" ), build( true, 0, "g3" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 2)" ), build( true, 0, "g3" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 3)" ), build( false, 1, "(2, 3)" ) },
                                   { build( false, 1, "(0, 4)" ), build( false, 1, "(1, 4)" ), build( false, 1, "(2, 4)" ) }
                           } );

        //Group g2 - should split g1 but not g3
        data.collapseCell( 0,
                           1 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, false },
                           new boolean[]{ false, true, false, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( false, 1, "(0, 0)" ), build( true, 2, "g2" ), build( true, 3, "g3" ) },
                                   { build( false, 1, "g1" ), build( true, 0, "g2" ), build( true, 0, "g3" ) },
                                   { build( true, 2, "g1" ), build( false, 1, "(1, 2)" ), build( true, 0, "g3" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 3)" ), build( false, 1, "(2, 3)" ) },
                                   { build( false, 1, "(0, 4)" ), build( false, 1, "(1, 4)" ), build( false, 1, "(2, 4)" ) }
                           } );

        //Group g1 - should split g3
        data.collapseCell( 2,
                           0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, false },
                           new boolean[]{ false, true, false, true, false },
                           new BaseGridTest.Expected[][]{
                                   { build( false, 1, "(0, 0)" ), build( true, 2, "g2" ), build( true, 2, "g3" ) },
                                   { build( false, 1, "g1" ), build( true, 0, "g2" ), build( true, 0, "g3" ) },
                                   { build( true, 2, "g1" ), build( false, 1, "(1, 2)" ), build( false, 1, "g3" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 3)" ), build( false, 1, "(2, 3)" ) },
                                   { build( false, 1, "(0, 4)" ), build( false, 1, "(1, 4)" ), build( false, 1, "(2, 4)" ) }
                           } );

        //Ungroup g1 - g3 should remain split as we don't merge into collapsed cells
        data.expandCell( 2,
                         0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, false },
                           new boolean[]{ false, true, false, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( false, 1, "(0, 0)" ), build( true, 2, "g2" ), build( true, 2, "g3" ) },
                                   { build( false, 1, "g1" ), build( true, 0, "g2" ), build( true, 0, "g3" ) },
                                   { build( true, 2, "g1" ), build( false, 1, "(1, 2)" ), build( false, 1, "g3" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 3)" ), build( false, 1, "(2, 3)" ) },
                                   { build( false, 1, "(0, 4)" ), build( false, 1, "(1, 4)" ), build( false, 1, "(2, 4)" ) }
                           } );

        //Group g1 (again) - there should be no change in state, other than an additional collapsed row
        data.collapseCell( 2,
                           0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, true, true, false },
                           new boolean[]{ false, true, false, true, false },
                           new BaseGridTest.Expected[][]{
                                   { build( false, 1, "(0, 0)" ), build( true, 2, "g2" ), build( true, 2, "g3" ) },
                                   { build( false, 1, "g1" ), build( true, 0, "g2" ), build( true, 0, "g3" ) },
                                   { build( true, 2, "g1" ), build( false, 1, "(1, 2)" ), build( false, 1, "g3" ) },
                                   { build( true, 0, "g1" ), build( false, 1, "(1, 3)" ), build( false, 1, "(2, 3)" ) },
                                   { build( false, 1, "(0, 4)" ), build( false, 1, "(1, 4)" ), build( false, 1, "(2, 4)" ) }
                           } );
    }

    @Test
    public void testGroupUpdateCellValue() {
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

        //Group cells
        data.collapseCell( 0,
                           0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, false },
                           new boolean[]{ false, true, false },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 2, "(0, 0)" ), build( false, 1, "(1, 0)" ) },
                                   { build( true, 0, "(0, 0)" ), build( false, 1, "(1, 1)" ) },
                                   { build( false, 1, "(0, 2)" ), build( false, 1, "(1, 2)" ) }
                           } );

        //Update cell value
        data.setCell( 0,
                      0,
                      new BaseGridCellValue<String>( "<changed>" ) );

        //Ungroup cells
        data.expandCell( 0,
                         0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, false },
                           new boolean[]{ false, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 2, "<changed>" ), build( false, 1, "(1, 0)" ) },
                                   { build( true, 0, "<changed>" ), build( false, 1, "(1, 1)" ) },
                                   { build( false, 1, "(0, 2)" ), build( false, 1, "(1, 2)" ) }
                           } );
    }

    @Test
    public void testGroupMovedColumnUpdateCellValue() {
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

        //Group cells
        data.collapseCell( 0,
                           0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, false },
                           new boolean[]{ false, true, false },
                           new BaseGridTest.Expected[][]{
                                   { build( true, 2, "(0, 0)" ), build( false, 1, "(1, 0)" ) },
                                   { build( true, 0, "(0, 0)" ), build( false, 1, "(1, 1)" ) },
                                   { build( false, 1, "(0, 2)" ), build( false, 1, "(1, 2)" ) }
                           } );

        //Move column
        data.moveColumnTo( 1,
                           gc1 );

        //Update cell value
        data.setCell( 0,
                      1,
                      new BaseGridCellValue<String>( "<changed>" ) );

        //Ungroup cells
        data.expandCell( 0,
                         1 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, false },
                           new boolean[]{ false, false, false },
                           new BaseGridTest.Expected[][]{
                                   { build( false, 1, "(1, 0)" ), build( true, 2, "<changed>" ) },
                                   { build( false, 1, "(1, 1)" ), build( true, 0, "<changed>" ) },
                                   { build( false, 1, "(1, 2)" ), build( false, 1, "(0, 2)" ) }
                           } );
    }

    @Test
    public void testMergedDeleteCellValue() {
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

        //Group cells
        data.collapseCell( 0,
                           0 );

        assertGridIndexes( data,
                           new boolean[]{ true, true, false },
                           new boolean[]{ false, true, false },
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

}