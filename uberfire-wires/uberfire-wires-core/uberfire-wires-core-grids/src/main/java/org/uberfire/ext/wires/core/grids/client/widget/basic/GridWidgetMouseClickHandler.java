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

import org.uberfire.ext.wires.core.grids.client.model.basic.GridData;
import org.uberfire.ext.wires.core.grids.client.widget.BaseGridWidgetMouseClickHandler;
import org.uberfire.ext.wires.core.grids.client.widget.ISelectionManager;
import org.uberfire.ext.wires.core.grids.client.widget.renderers.IGridRenderer;

/**
 * MouseClickHandler for a Grid containing non-merged cells
 */
public class GridWidgetMouseClickHandler extends BaseGridWidgetMouseClickHandler<GridWidget> {

    public GridWidgetMouseClickHandler( final GridWidget gridWidget,
                                        final ISelectionManager selectionManager,
                                        final IGridRenderer<GridData> renderer ) {
        super( gridWidget,
               selectionManager,
               renderer );
    }

}
