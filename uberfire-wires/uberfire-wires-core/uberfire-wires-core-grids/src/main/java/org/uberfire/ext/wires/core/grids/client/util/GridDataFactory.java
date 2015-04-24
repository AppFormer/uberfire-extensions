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
package org.uberfire.ext.wires.core.grids.client.util;

import org.uberfire.ext.wires.core.grids.client.model.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.basic.GridData;
import org.uberfire.ext.wires.core.grids.client.model.basic.GridRow;
import org.uberfire.ext.wires.core.grids.client.model.mergable.MergableGridData;
import org.uberfire.ext.wires.core.grids.client.model.mergable.MergableGridRow;

/**
 * Utility class to fill the example grids in this Showcase demo
 */
public class GridDataFactory {

    public static double FILL_FACTOR = 0.75;

    /**
     * Populate a non-merged grid. Columns should already have been appended.
     * @param grid The grid to populate
     * @param rowCount The number of required rows
     */
    @SuppressWarnings("unused")
    public static void populate( final GridData grid,
                                 final int rowCount ) {
        final int columnCount = grid.getColumns().size();
        for ( int rowIndex = 0; rowIndex < rowCount; rowIndex++ ) {
            final GridRow row = new GridRow();
            row.setHeight( getRowHeight() );
            grid.appendRow( row );
            for ( int columnIndex = 0; columnIndex < columnCount; columnIndex++ ) {
                if ( Math.random() < FILL_FACTOR ) {
                    grid.setCell( rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<String>( "(" + columnIndex + ", " + rowIndex + ")" ) );
                }
            }
        }
    }

    /**
     * Populate a merged grid. Columns should already have been appended.
     * @param grid The grid to populate
     * @param rowCount The number of required rows
     */
    public static void populate( final MergableGridData grid,
                                 final int rowCount ) {
        final int columnCount = grid.getColumns().size();
        for ( int rowIndex = 0; rowIndex < rowCount; rowIndex++ ) {
            final MergableGridRow row = new MergableGridRow( getRowHeight() );
            grid.appendRow( row );
            for ( int columnIndex = 0; columnIndex < columnCount; columnIndex++ ) {
                if ( Math.random() < FILL_FACTOR ) {
                    grid.setCell( rowIndex,
                                  columnIndex,
                                  new BaseGridCellValue<String>( "(" + columnIndex + ", " + rowIndex + ")" ) );
                }
            }
        }
    }

    //Pick one of three random row heights
    private static double getRowHeight() {
        final int r = (int) Math.round( Math.random() * 3 );
        switch ( r ) {
            case 0:
                return 20.0;
            case 1:
                return 40.0;
        }
        return 60.0;
    }

}
