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
package org.uberfire.ext.wires.core.grids.client.widget;

import java.util.List;

import com.ait.lienzo.client.core.event.NodeMouseDoubleClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseDoubleClickHandler;
import com.ait.lienzo.client.core.types.Point2D;
import org.uberfire.ext.wires.core.grids.client.model.IGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.IGridData;
import org.uberfire.ext.wires.core.grids.client.model.IGridRow;
import org.uberfire.ext.wires.core.grids.client.util.GridCoordinateUtils;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.renderers.IGridRenderer;

/**
 * Base MousedoubleClickHandler to handle double-clicks to either the GridWidgets Header or Body. This
 * implementation supports double-clicking on a cell in the Body and delegating a response to the
 * sub-classes {code}onDoubleClick(){code} method.
 * @param <W> The GridWidget to which this MouseClickHandler is attached.
 * @param <M> The GridWidget's underlying Model
 */
public abstract class BaseGridWidgetMouseDoubleClickHandler<W extends BaseGridWidget<M, ?>, M extends IGridData<?, ?, ?>> implements NodeMouseDoubleClickHandler {

    protected W gridWidget;
    protected ISelectionManager selectionManager;
    protected IGridRenderer<?> renderer;

    public BaseGridWidgetMouseDoubleClickHandler( final W gridWidget,
                                                  final ISelectionManager selectionManager,
                                                  final IGridRenderer<?> renderer ) {
        this.gridWidget = gridWidget;
        this.selectionManager = selectionManager;
        this.renderer = renderer;
    }

    @Override
    public void onNodeMouseDoubleClick( final NodeMouseDoubleClickEvent event ) {
        handleHeaderCellDoubleClick( event );
        handleBodyCellDoubleClick( event );
    }

    /**
     * Does nothing by default, but allows sub-classes to provide their own behaviour.
     * @param event
     */
    protected void handleHeaderCellDoubleClick( final NodeMouseDoubleClickEvent event ) {
        //Do nothing by default
    }

    /**
     * Check if a MouseDoubleClickEvent happened within a cell and delegate a response
     * to sub-classes {code}doeEdit(){code} method, passing a context object that can
     * be used to determine the cell that was double-clicked.
     * @param event
     */
    protected void handleBodyCellDoubleClick( final NodeMouseDoubleClickEvent event ) {
        //Convert Canvas co-ordinate to Grid co-ordinate
        final Point2D ap = GridCoordinateUtils.mapToGridWidgetAbsolutePoint( gridWidget,
                                                                             new Point2D( event.getX(),
                                                                                          event.getY() ) );
        final double x = ap.getX();
        final double y = ap.getY();
        if ( x < 0 || x > gridWidget.getWidth() ) {
            return;
        }
        if ( y < renderer.getHeaderHeight() || y > gridWidget.getHeight() ) {
            return;
        }

        final M model = gridWidget.getModel();

        //Get row index
        IGridRow<?> row;
        int rowIndex = 0;
        double offsetY = y - renderer.getHeaderHeight();
        while ( ( row = model.getRow( rowIndex ) ).getHeight() < offsetY ) {
            offsetY = offsetY - row.getHeight();
            rowIndex++;
        }
        if ( rowIndex < 0 || rowIndex > model.getRowCount() - 1 ) {
            return;
        }

        //Get column index
        int columnIndex = -1;
        double offsetX = 0;
        final List<? extends IGridColumn<?, ?>> columns = model.getColumns();
        for ( int idx = 0; idx < columns.size(); idx++ ) {
            final IGridColumn<?, ?> gridColumn = columns.get( idx );
            if ( gridColumn.isVisible() ) {
                final double width = gridColumn.getWidth();
                if ( x > offsetX && x < offsetX + width ) {
                    columnIndex = idx;
                    break;
                }
                offsetX = offsetX + width;
            }
        }
        if ( columnIndex < 0 || columnIndex > columns.size() - 1 ) {
            return;
        }

        final double cellX = gridWidget.getX() + offsetX;
        final double cellY = gridWidget.getY() + renderer.getHeaderHeight() + getRowOffset( rowIndex,
                                                                                            columnIndex,
                                                                                            model );
        final double cellHeight = getCellHeight( rowIndex,
                                                 columnIndex,
                                                 model );

        final GridCellRenderContext context = new GridCellRenderContext( cellX,
                                                                         cellY,
                                                                         columns.get( columnIndex ).getWidth(),
                                                                         cellHeight,
                                                                         rowIndex,
                                                                         columnIndex,
                                                                         gridWidget.getViewport().getTransform(),
                                                                         gridWidget );

        onDoubleClick( context );
    }

    /**
     * Get the y-coordinate of the row relative to the grid. i.e. 0 <= offset <= gridHeight.
     * This may be different to the underlying model's {code}getRowOffset(){code} for merged cells.
     * @param rowIndex The index of the row on which the MouseDoubleClickEvent occurred.
     * @param columnIndex The index of the column in which the MouseDoubleClickEvent occurred.
     * @param model The GridWidget's underlying Model
     * @return
     */
    protected abstract double getRowOffset( final int rowIndex,
                                            final int columnIndex,
                                            final M model );

    /**
     * Get the height of a cell. This may be different to the row's height for merged cells.
     * @param rowIndex The index of the row on which the MouseDoubleClickEvent occurred.
     * @param columnIndex The index of the column in which the MouseDoubleClickEvent occurred.
     * @param model The GridWidget's underlying Model
     * @return
     */
    protected abstract double getCellHeight( final int rowIndex,
                                             final int columnIndex,
                                             final M model );

    /**
     * Signal a MouseDoubleClickEvent has occurred on a cell in the Body.
     * Information regarding the cell, cell's dimensions etc are provided
     * in the render context.
     * @param context
     */
    protected abstract void onDoubleClick( final GridCellRenderContext context );

}
