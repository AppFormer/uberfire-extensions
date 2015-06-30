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
package org.uberfire.ext.wires.core.grids.client.widget.dnd;

import java.util.List;
import java.util.Map;

import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.event.NodeMouseMoveHandler;
import com.ait.lienzo.client.core.mediator.IMediator;
import com.ait.lienzo.client.core.types.Point2D;
import com.google.gwt.dom.client.Style;
import org.uberfire.ext.wires.core.grids.client.model.IGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.IGridData;
import org.uberfire.ext.wires.core.grids.client.util.GridCoordinateUtils;
import org.uberfire.ext.wires.core.grids.client.widget.BaseGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.renderers.IGridRenderer;

/**
 * MouseMoveHandler to handle to detect potential drag operations and handle the drag itself; if required.
 */
public class GridWidgetMouseMoveHandler implements NodeMouseMoveHandler {

    // How close the mouse pointer needs to be to the column separator to initiate a resize operation.
    private static final int COLUMN_RESIZE_HANDLE_SENSITIVITY = 5;

    private final GridLayer layer;
    private final GridWidgetHandlersState state;
    private final Map<IGridData<?, ?, ?>, BaseGridWidget<?, ?>> selectables;

    public GridWidgetMouseMoveHandler( final GridLayer layer,
                                       final GridWidgetHandlersState state,
                                       final Map<IGridData<?, ?, ?>, BaseGridWidget<?, ?>> selectables ) {
        this.layer = layer;
        this.state = state;
        this.selectables = selectables;
    }

    @Override
    public void onNodeMouseMove( final NodeMouseMoveEvent event ) {
        switch ( state.getOperation() ) {
            case COLUMN_RESIZE:
                //If we're currently resizing a column we don't need to find a column
                handleColumnResize( event );
                break;

            case COLUMN_MOVE:
                //If we're currently moving a column we don't need to find a column
                handleColumnMove( event );
                break;

            default:
                //Otherwise try to find a Grid and GridColumn
                state.setGrid( null );
                state.setGridColumn( null );
                state.setOperation( GridWidgetHandlersState.GridWidgetHandlersOperation.NONE );
                state.setCursor( Style.Cursor.DEFAULT );

                for ( Map.Entry<IGridData<?, ?, ?>, BaseGridWidget<?, ?>> e : selectables.entrySet() ) {
                    final IGridData<?, ?, ?> grid = e.getKey();
                    final BaseGridWidget<?, ?> gridWidget = e.getValue();
                    final IGridRenderer<?> renderer = gridWidget.getRenderer();
                    final Point2D ap = GridCoordinateUtils.mapToGridWidgetAbsolutePoint( gridWidget,
                                                                                         new Point2D( event.getX(),
                                                                                                      event.getY() ) );

                    final double ax = ap.getX();
                    final double ay = ap.getY();
                    if ( ax < 0 || ax > gridWidget.getWidth() ) {
                        continue;
                    }
                    if ( ay < 0 || ay > gridWidget.getHeight() ) {
                        continue;
                    } else if ( ay < renderer.getHeaderHeight() ) {
                        double offsetX = 0;
                        for ( IGridColumn gc : grid.getColumns() ) {
                            //Check for column moving
                            if ( gc.isVisible() ) {
                                final double columnWidth = gc.getWidth();
                                if ( gc.isMoveable() ) {
                                    if ( ax > offsetX && ax < offsetX + columnWidth ) {
                                        state.setGrid( grid );
                                        state.setGridColumn( gc );
                                        state.setOperation( GridWidgetHandlersState.GridWidgetHandlersOperation.COLUMN_MOVE_PENDING );
                                        state.setCursor( Style.Cursor.MOVE );
                                        break;
                                    }
                                }
                                offsetX = offsetX + columnWidth;
                            }
                        }

                    } else {
                        double offsetX = 0;
                        for ( IGridColumn gc : grid.getColumns() ) {
                            //Check for column resizing
                            if ( gc.isVisible() ) {
                                final double columnWidth = gc.getWidth();
                                if ( gc.isResizable() ) {
                                    if ( ax > columnWidth + offsetX - COLUMN_RESIZE_HANDLE_SENSITIVITY && ax < columnWidth + offsetX + COLUMN_RESIZE_HANDLE_SENSITIVITY ) {
                                        state.setGrid( grid );
                                        state.setGridColumn( gc );
                                        state.setOperation( GridWidgetHandlersState.GridWidgetHandlersOperation.COLUMN_RESIZE_PENDING );
                                        state.setCursor( Style.Cursor.COL_RESIZE );
                                        break;
                                    }
                                }
                                offsetX = offsetX + columnWidth;
                            }
                        }
                    }
                }

                layer.getViewport().getElement().getStyle().setCursor( state.getCursor() );
                for ( IMediator mediator : layer.getViewport().getMediators() ) {
                    mediator.setEnabled( state.getGrid() == null );
                }
        }
    }

    private void handleColumnResize( final NodeMouseMoveEvent event ) {
        final IGridColumn gridColumn = state.getGridColumn();
        final BaseGridWidget<?, ?> gridWidget = selectables.get( state.getGrid() );
        final Point2D ap = GridCoordinateUtils.mapToGridWidgetAbsolutePoint( gridWidget,
                                                                             new Point2D( event.getX(),
                                                                                          event.getY() ) );
        final double deltaX = ap.getX() - state.getEventInitialX();
        final Double columnMinimumWidth = gridColumn.getMinimumColumnWidth();
        final Double columnMaxiumumWidth = gridColumn.getMaximumColumnWidth();
        double columnNewWidth = state.getEventInitialColumnWidth() + deltaX;
        if ( columnMinimumWidth != null ) {
            if ( columnNewWidth < columnMinimumWidth ) {
                columnNewWidth = columnMinimumWidth;
            }
        }
        if ( columnMaxiumumWidth != null ) {
            if ( columnNewWidth > columnMaxiumumWidth ) {
                columnNewWidth = columnMaxiumumWidth;
            }
        }
        gridColumn.setWidth( columnNewWidth );
        layer.draw();
        return;
    }

    private void handleColumnMove( final NodeMouseMoveEvent event ) {
        final IGridData grid = state.getGrid();
        final IGridColumn gridColumn = state.getGridColumn();
        final List<? extends IGridColumn<?, ?>> columns = grid.getColumns();

        final BaseGridWidget gridWidget = selectables.get( grid );
        final Point2D ap = GridCoordinateUtils.mapToGridWidgetAbsolutePoint( gridWidget,
                                                                             new Point2D( event.getX(),
                                                                                          event.getY() ) );
        final double ax = ap.getX();

        double offsetX = 0;
        for ( int index = 0; index < columns.size(); index++ ) {
            final IGridColumn gc = columns.get( index );
            if ( gc.isVisible() ) {
                final double columnWidth = gc.getWidth();
                if ( gridColumn.getColumnGroup().equals( gc.getColumnGroup() ) ) {
                    final double columnMovedWidth = gridColumn.getWidth();
                    final double minColX = Math.max( offsetX, offsetX + ( columnWidth - columnMovedWidth ) / 2 );
                    final double maxColX = Math.min( offsetX + columnWidth, offsetX + ( columnWidth + columnMovedWidth ) / 2 );
                    if ( ax > minColX && ax < maxColX ) {
                        grid.moveColumnTo( index,
                                           gridColumn );
                        state.getEventColumnHighlight().setX( gridWidget.getX() + gridWidget.getModel().getColumnOffset( gridColumn ) );
                        layer.draw();
                        break;
                    }
                }
                offsetX = offsetX + columnWidth;
            }
        }
    }

}
