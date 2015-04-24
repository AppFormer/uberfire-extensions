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
package org.uberfire.ext.wires.core.grids.client.widget.renderers;

import com.ait.lienzo.client.core.shape.Group;
import org.uberfire.ext.wires.core.grids.client.model.IGridData;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderRenderContext;

/**
 * Definition of a render for the pluggable rendering mechanism.
 * @param <M> The data model for the GridWidget data being rendered.
 */
public interface IGridRenderer<M extends IGridData<?, ?, ?>> {

    /**
     * Get a display name for the renderer
     * @return
     */
    String getName();

    /**
     * Get the height of the header built by this renderer.
     * @return
     */
    double getHeaderHeight();

    /**
     * Render a "selector" when a grid has been selected, i.e. clicked.
     * @param width The width of the GridWidget.
     * @param height The height of the GridWidget including header and body.
     * @return
     */
    Group renderSelector( final double width,
                          final double height );

    /**
     * Render the header for the Grid.
     * @param model The data model for the GridWidget.
     * @param context The context of the render phase.
     * @return A Group containing all Shapes representing the Header.
     */
    Group renderHeader( final M model,
                        final GridHeaderRenderContext context );

    /**
     * Render the body for the Grid.
     * @param model The data model for the GridWidget.
     * @param context The context of the render phase.
     * @return A Group containing all Shapes representing the Body.
     */
    Group renderBody( final M model,
                      final GridBodyRenderContext context );

}
