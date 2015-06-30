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

import java.util.Map;

import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.event.NodeMouseUpHandler;
import com.google.gwt.dom.client.Style;
import org.uberfire.ext.wires.core.grids.client.model.IGridData;
import org.uberfire.ext.wires.core.grids.client.widget.BaseGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.GridLayer;

/**
 * MouseUpHandler to handle completion of drag operations and release resources.
 */
public class GridWidgetMouseUpHandler implements NodeMouseUpHandler {

    private final GridLayer layer;
    private final GridWidgetHandlersState state;
    private final Map<IGridData<?, ?, ?>, BaseGridWidget<?, ?>> selectables;

    public GridWidgetMouseUpHandler( final GridLayer layer,
                                     final GridWidgetHandlersState state,
                                     final Map<IGridData<?, ?, ?>, BaseGridWidget<?, ?>> selectables ) {
        this.layer = layer;
        this.state = state;
        this.selectables = selectables;
    }

    @Override
    public void onNodeMouseUp( final NodeMouseUpEvent event ) {
        switch ( state.getOperation() ) {
            case NONE:
            case COLUMN_MOVE_PENDING:
            case COLUMN_RESIZE_PENDING:
            case COLUMN_RESIZE:
                break;
            case COLUMN_MOVE:
                //Clean-up the GridWidgetColumnProxy
                layer.remove( state.getEventColumnHighlight() );
                layer.draw();
        }

        //Reset state
        state.setGrid( null );
        state.setGridColumn( null );
        state.setOperation( GridWidgetHandlersState.GridWidgetHandlersOperation.NONE );
        state.setCursor( Style.Cursor.DEFAULT );
        layer.getViewport().getElement().getStyle().setCursor( state.getCursor() );
    }

}
