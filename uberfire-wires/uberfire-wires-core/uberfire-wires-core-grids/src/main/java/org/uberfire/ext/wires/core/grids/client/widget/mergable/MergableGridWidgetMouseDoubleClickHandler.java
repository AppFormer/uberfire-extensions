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
package org.uberfire.ext.wires.core.grids.client.widget.mergable;

import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.wires.core.grids.client.model.IGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.mergable.MergableGridCell;
import org.uberfire.ext.wires.core.grids.client.model.mergable.MergableGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.mergable.MergableGridData;
import org.uberfire.ext.wires.core.grids.client.widget.BaseGridWidgetMouseDoubleClickHandler;
import org.uberfire.ext.wires.core.grids.client.widget.ISelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.renderers.mergable.IMergableGridRenderer;

/**
 * MouseDoubleClickHandler for a Grid containing mergable cells.
 */
public class MergableGridWidgetMouseDoubleClickHandler extends BaseGridWidgetMouseDoubleClickHandler<MergableGridWidget, MergableGridData> {

    public MergableGridWidgetMouseDoubleClickHandler( final MergableGridWidget gridWidget,
                                                      final ISelectionManager selectionManager,
                                                      final IMergableGridRenderer renderer ) {
        super( gridWidget,
               selectionManager,
               renderer );
    }

    @Override
    protected double getRowOffset( final int rowIndex,
                                   final int columnIndex,
                                   final MergableGridData model ) {
        final MergableGridCell<?> cell = model.getCell( rowIndex,
                                                        columnIndex );
        if ( cell == null ) {
            return model.getRowOffset( rowIndex );
        }
        if ( cell.getMergedCellCount() == 1 ) {
            return model.getRowOffset( rowIndex );
        } else if ( cell.getMergedCellCount() > 1 ) {
            return model.getRowOffset( rowIndex );
        } else {
            int _rowIndex = rowIndex;
            MergableGridCell<?> _cell = cell;
            while ( _cell.getMergedCellCount() == 0 ) {
                _rowIndex--;
                _cell = model.getCell( _rowIndex,
                                       columnIndex );
            }
            return model.getRowOffset( _rowIndex );
        }
    }

    @Override
    protected double getCellHeight( final int rowIndex,
                                    final int columnIndex,
                                    final MergableGridData model ) {
        final MergableGridCell<?> cell = model.getCell( rowIndex,
                                                        columnIndex );
        if ( cell == null ) {
            return model.getRow( rowIndex ).getHeight();
        }
        if ( cell.getMergedCellCount() == 1 ) {
            return model.getRow( rowIndex ).getHeight();
        } else if ( cell.getMergedCellCount() > 1 ) {
            double height = 0;
            for ( int i = rowIndex; i < rowIndex + cell.getMergedCellCount(); i++ ) {
                height = height + model.getRow( i ).getHeight();
            }
            return height;
        } else {
            int _rowIndex = rowIndex;
            MergableGridCell<?> _cell = cell;
            while ( _cell.getMergedCellCount() == 0 ) {
                _rowIndex--;
                _cell = model.getCell( _rowIndex,
                                       columnIndex );
            }
            double height = 0;
            for ( int i = _rowIndex; i < _rowIndex + _cell.getMergedCellCount(); i++ ) {
                height = height + model.getRow( i ).getHeight();
            }
            return height;
        }
    }

    @Override
    protected void onDoubleClick( final GridCellRenderContext context ) {
        final int rowIndex = context.getRowIndex();
        final int columnIndex = context.getColumnIndex();
        final MergableGridCell cell = gridWidget.getModel().getCell( rowIndex,
                                                                     columnIndex );
        final MergableGridColumn column = gridWidget.getModel().getColumns().get( columnIndex );
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
