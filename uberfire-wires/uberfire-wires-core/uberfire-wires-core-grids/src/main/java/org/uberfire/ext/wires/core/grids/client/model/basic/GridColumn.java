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

import com.ait.lienzo.client.core.shape.Group;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.wires.core.grids.client.model.BaseGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.IGridCellValue;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridCellRenderContext;

/**
 * Base implementation of a IGridColumn for a grid that cannot contain merged cells.
 * @param <T> The Type of value presented by this column
 */
public abstract class GridColumn<T> extends BaseGridColumn<GridRow, GridCell<?>> {

    public GridColumn( final String title,
                       final double width ) {
        super( title,
               width );
    }

    @Override
    public Group renderCell( final GridRow row,
                             final GridCellRenderContext context ) {
        final GridCell cell = row.getCells().get( getIndex() );
        if ( cell == null ) {
            return null;
        }
        final Group g = new Group();
        renderCell( g,
                    cell,
                    context );
        return g;
    }

    public abstract void renderCell( final Group g,
                                     final GridCell<T> cell,
                                     final GridCellRenderContext context );

    public void edit( final GridCell<T> cell,
                      final GridCellRenderContext context,
                      final Callback<IGridCellValue<T>> callback ) {
        //Do nothing by default
    }

}
