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

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import com.ait.lienzo.client.core.event.NodeMouseClickHandler;
import com.ait.lienzo.client.core.types.Point2D;
import org.uberfire.ext.wires.core.grids.client.model.IGridColumn;
import org.uberfire.ext.wires.core.grids.client.util.GridCoordinateUtils;
import org.uberfire.ext.wires.core.grids.client.widget.renderers.IGridRenderer;

/**
 * Base MouseClickHandler to handle clicks to either the GridWidgets Header or Body. This implementation
 * supports clicking on a "linked" column in the Header and delegating a response to the ISelectionManager.
 * @param <W> The GridWidget to which this MouseClickHandler is attached.
 */
public abstract class BaseGridWidgetMouseClickHandler<W extends BaseGridWidget<?, ?>> implements NodeMouseClickHandler {

    protected W gridWidget;
    protected ISelectionManager selectionManager;
    protected IGridRenderer<?> renderer;

    public BaseGridWidgetMouseClickHandler( final W gridWidget,
                                            final ISelectionManager selectionManager,
                                            final IGridRenderer<?> renderer ) {
        this.gridWidget = gridWidget;
        this.selectionManager = selectionManager;
        this.renderer = renderer;
    }

    @Override
    public void onNodeMouseClick( final NodeMouseClickEvent event ) {
        selectionManager.select( gridWidget.getModel() );
        handleHeaderCellClick( event );
        handleBodyCellClick( event );
    }

    /**
     * Check if a MouseClickEvent happened on a "linked" column. If it does then
     * delegate a response to ISelectionManager.
     * @param event
     */
    protected void handleHeaderCellClick( final NodeMouseClickEvent event ) {
        //Convert Canvas co-ordinate to Grid co-ordinate
        final Point2D ap = GridCoordinateUtils.mapToGridWidgetAbsolutePoint( gridWidget,
                                                                             new Point2D( event.getX(),
                                                                                          event.getY() ) );
        final double x = ap.getX();
        final double y = ap.getY();
        if ( x < 0 || x > gridWidget.getWidth() ) {
            return;
        }
        if ( y < 0 || y > renderer.getHeaderHeight() ) {
            return;
        }

        //Get column index
        double offsetX = 0;
        IGridColumn<?, ?> column = null;
        for ( IGridColumn<?, ?> gridColumn : gridWidget.getModel().getColumns() ) {
            if ( gridColumn.isVisible() ) {
                if ( x > offsetX && x < offsetX + gridColumn.getWidth() ) {
                    column = gridColumn;
                    break;
                }
                offsetX = offsetX + gridColumn.getWidth();
            }
        }
        if ( column == null ) {
            return;
        }

        //If linked scroll it into view
        if ( column.isLinked() ) {
            final IGridColumn<?, ?> link = column.getLink();
            selectionManager.selectLinkedColumn( link );
        }
    }

    /**
     * Does nothing by default, but allows sub-classes to provide their own behaviour.
     * @param event
     */
    protected void handleBodyCellClick( final NodeMouseClickEvent event ) {
        //Do nothing by default
    }

}
