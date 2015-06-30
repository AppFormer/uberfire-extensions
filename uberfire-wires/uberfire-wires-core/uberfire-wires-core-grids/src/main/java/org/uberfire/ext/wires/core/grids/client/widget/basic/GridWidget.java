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

import com.ait.lienzo.client.core.event.NodeMouseClickEvent;
import org.uberfire.ext.wires.core.grids.client.model.basic.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.BaseGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.ISelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.renderers.IGridRenderer;

/**
 * A Grid that contains non-mergable cells
 */
public class GridWidget extends BaseGridWidget<GridData, IGridRenderer<GridData>> {

    private final GridWidgetMouseClickHandler mouseClickHandler;
    private final GridWidgetMouseDoubleClickHandler mouseDoubleClickHandler;

    public GridWidget( final GridData model,
                       final ISelectionManager selectionManager,
                       final IGridRenderer<GridData> renderer ) {
        super( model,
               selectionManager,
               renderer );

        //Click handlers
        mouseClickHandler = new GridWidgetMouseClickHandler( this,
                                                             selectionManager,
                                                             renderer );
        mouseDoubleClickHandler = new GridWidgetMouseDoubleClickHandler( this,
                                                                         selectionManager,
                                                                         renderer );
        addNodeMouseClickHandler( mouseClickHandler );
        addNodeMouseDoubleClickHandler( mouseDoubleClickHandler );
    }

    @Override
    public void onNodeMouseClick( final NodeMouseClickEvent event ) {
        mouseClickHandler.onNodeMouseClick( event );
    }

}
