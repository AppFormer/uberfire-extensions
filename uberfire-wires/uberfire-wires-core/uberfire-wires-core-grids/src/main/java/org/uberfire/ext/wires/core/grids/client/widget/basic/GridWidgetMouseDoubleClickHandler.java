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
package org.uberfire.ext.wires.core.grids.client.widget.basic;

import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.wires.core.grids.client.model.IGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.basic.GridCell;
import org.uberfire.ext.wires.core.grids.client.model.basic.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.basic.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.BaseGridWidgetMouseDoubleClickHandler;
import org.uberfire.ext.wires.core.grids.client.widget.ISelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.renderers.IGridRenderer;

/**
 * MouseDoubleClickHandler for a Grid containing non-mergable cells.
 */
public class GridWidgetMouseDoubleClickHandler extends BaseGridWidgetMouseDoubleClickHandler<GridWidget, GridData> {

    public GridWidgetMouseDoubleClickHandler( final GridWidget gridWidget,
                                              final ISelectionManager selectionManager,
                                              final IGridRenderer<GridData> renderer ) {
        super( gridWidget,
               selectionManager,
               renderer );
    }

    @Override
    @SuppressWarnings("unused")
    protected double getRowOffset( final int rowIndex,
                                   final int columnIndex,
                                   final GridData model ) {
        return model.getRowOffset( rowIndex );
    }

    @Override
    @SuppressWarnings("unused")
    protected double getCellHeight( final int rowIndex,
                                    final int columnIndex,
                                    final GridData model ) {
        return model.getRow( rowIndex ).getHeight();
    }

    @Override
    protected void onDoubleClick( final GridCellRenderContext context ) {
        final int rowIndex = context.getRowIndex();
        final int columnIndex = context.getColumnIndex();
        final GridCell cell = gridWidget.getModel().getCell( rowIndex,
                                                             columnIndex );
        final GridColumn column = gridWidget.getModel().getColumns().get( columnIndex );
        column.edit( cell,
                     context,
                     new Callback<IGridCellValue<?>>() {

                         @Override
                         public void callback( final IGridCellValue<?> value ) {
                             gridWidget.getModel().setCell( rowIndex,
                                                            columnIndex,
                                                            value );
                             gridWidget.getLayer().draw();
                         }
                     } );
    }

}
