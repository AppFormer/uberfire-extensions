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
package org.uberfire.ext.wires.core.grids.client.model.basic;

import java.util.List;

import com.ait.lienzo.client.core.shape.Group;
import org.junit.Test;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.wires.core.grids.client.model.IGridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridCellRenderContext;

import static org.junit.Assert.*;

public class GridIndexingTest {

    @Test
    public void testAddInitialColumns() {
        final GridData grid = new GridData();
        final GridColumn<String> gc1 = new MockGridColumn<String>( "col1",
                                                                   100 );
        final GridColumn<String> gc2 = new MockGridColumn<String>( "col2",
                                                                   100 );
        grid.appendColumn( gc1 );
        grid.appendColumn( gc2 );

        final List<GridColumn<?>> columns = grid.getColumns();

        assertEquals( 2,
                      columns.size() );
        assertEquals( 0,
                      gc1.getIndex() );
        assertEquals( 1,
                      gc2.getIndex() );
        assertEquals( columns.get( 0 ),
                      gc1 );
        assertEquals( columns.get( 1 ),
                      gc2 );
    }

    @Test
    public void testAddColumn() {
        final GridData grid = new GridData();
        final GridColumn<String> gc1 = new MockGridColumn<String>( "col1",
                                                                   100 );
        final GridColumn<String> gc2 = new MockGridColumn<String>( "col2",
                                                                   100 );
        final GridColumn<String> gc3 = new MockGridColumn<String>( "col3",
                                                                   100 );
        grid.appendColumn( gc1 );
        grid.appendColumn( gc2 );
        grid.insertColumn( 1,
                           gc3 );

        final List<GridColumn<?>> columns = grid.getColumns();

        assertEquals( 3,
                      columns.size() );
        assertEquals( 0,
                      gc1.getIndex() );
        assertEquals( 1,
                      gc2.getIndex() );
        assertEquals( 2,
                      gc3.getIndex() );
        assertEquals( columns.get( 0 ),
                      gc1 );
        assertEquals( columns.get( 1 ),
                      gc3 );
        assertEquals( columns.get( 2 ),
                      gc2 );
    }

    @Test
    public void testRemoveColumn() {
        final GridData grid = new GridData();
        final GridColumn<String> gc1 = new MockGridColumn<String>( "col1",
                                                                   100 );
        final GridColumn<String> gc2 = new MockGridColumn<String>( "col2",
                                                                   100 );
        final GridColumn<String> gc3 = new MockGridColumn<String>( "col3",
                                                                   100 );
        grid.appendColumn( gc1 );
        grid.appendColumn( gc2 );
        grid.appendColumn( gc3 );

        grid.removeColumn( gc2 );

        final List<GridColumn<?>> columns = grid.getColumns();

        assertEquals( 2,
                      columns.size() );
        assertEquals( 0,
                      gc1.getIndex() );
        assertEquals( 1,
                      gc2.getIndex() );
        assertEquals( 2,
                      gc3.getIndex() );
        assertEquals( columns.get( 0 ),
                      gc1 );
        assertEquals( columns.get( 1 ),
                      gc3 );
    }

    @Test
    public void testMoveColumnToLeft() {
        final GridData grid = new GridData();
        final GridColumn<String> gc1 = new MockGridColumn<String>( "col1",
                                                                   100 );
        final GridColumn<String> gc2 = new MockGridColumn<String>( "col2",
                                                                   100 );
        final GridColumn<String> gc3 = new MockGridColumn<String>( "col3",
                                                                   100 );
        final GridColumn<String> gc4 = new MockGridColumn<String>( "col4",
                                                                   100 );
        grid.appendColumn( gc1 );
        grid.appendColumn( gc2 );
        grid.appendColumn( gc3 );
        grid.appendColumn( gc4 );

        grid.moveColumnTo( 1,
                           gc4 );

        final List<GridColumn<?>> columns = grid.getColumns();

        assertEquals( 4,
                      columns.size() );
        assertEquals( 0,
                      gc1.getIndex() );
        assertEquals( 1,
                      gc2.getIndex() );
        assertEquals( 2,
                      gc3.getIndex() );
        assertEquals( 3,
                      gc4.getIndex() );
        assertEquals( columns.get( 0 ),
                      gc1 );
        assertEquals( columns.get( 1 ),
                      gc4 );
        assertEquals( columns.get( 2 ),
                      gc2 );
        assertEquals( columns.get( 3 ),
                      gc3 );
    }

    @Test
    public void testMoveColumnToRight() {
        final GridData grid = new GridData();
        final GridColumn<String> gc1 = new MockGridColumn<String>( "col1",
                                                                   100 );
        final GridColumn<String> gc2 = new MockGridColumn<String>( "col2",
                                                                   100 );
        final GridColumn<String> gc3 = new MockGridColumn<String>( "col3",
                                                                   100 );
        final GridColumn<String> gc4 = new MockGridColumn<String>( "col4",
                                                                   100 );
        grid.appendColumn( gc1 );
        grid.appendColumn( gc2 );
        grid.appendColumn( gc3 );
        grid.appendColumn( gc4 );

        grid.moveColumnTo( 3,
                           gc1 );

        final List<GridColumn<?>> columns = grid.getColumns();

        assertEquals( 4,
                      columns.size() );
        assertEquals( 0,
                      gc1.getIndex() );
        assertEquals( 1,
                      gc2.getIndex() );
        assertEquals( 2,
                      gc3.getIndex() );
        assertEquals( 3,
                      gc4.getIndex() );
        assertEquals( columns.get( 0 ),
                      gc2 );
        assertEquals( columns.get( 1 ),
                      gc3 );
        assertEquals( columns.get( 2 ),
                      gc4 );
        assertEquals( columns.get( 3 ),
                      gc1 );
    }

    static class MockGridColumn<T> extends GridColumn<T> {

        MockGridColumn( final String title,
                        final double width ) {
            super( title,
                   width );
        }

        @Override
        public void renderCell( final Group g,
                                final GridCell<T> cell,
                                final GridCellRenderContext context ) {
            //Do nothing
        }

        @Override
        public void edit( final GridCell<T> cell,
                          final GridCellRenderContext context,
                          final Callback<IGridCellValue<T>> callback ) {
            //Do nothing
        }

    }

}
