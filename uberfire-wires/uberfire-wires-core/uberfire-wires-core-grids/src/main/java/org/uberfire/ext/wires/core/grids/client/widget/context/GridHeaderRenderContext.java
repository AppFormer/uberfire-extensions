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
package org.uberfire.ext.wires.core.grids.client.widget.context;

import com.ait.lienzo.client.core.types.Transform;
import org.uberfire.ext.wires.core.grids.client.widget.BaseGridWidget;

/**
 * The context of a Grid's header during the rendering phase.
 */
public class GridHeaderRenderContext {

    private final int startColumnIndex;
    private final int endColumnIndex;
    private final double width;
    private final Transform transform;
    private final BaseGridWidget<?, ?> widget;

    public GridHeaderRenderContext( final int startColumnIndex,
                                    final int endColumnIndex,
                                    final double width,
                                    final Transform transform,
                                    final BaseGridWidget<?, ?> widget ) {
        this.startColumnIndex = startColumnIndex;
        this.endColumnIndex = endColumnIndex;
        this.width = width;
        this.transform = transform;
        this.widget = widget;
    }

    /**
     * The index of the first column being rendered.
     * @return
     */
    public int getStartColumnIndex() {
        return startColumnIndex;
    }

    /**
     * The index of the last column being rendered.
     * @return
     */
    public int getEndColumnIndex() {
        return endColumnIndex;
    }

    /**
     * The width of the Grid widget being rendered. This need not be the same as the Grid's width.
     * The rendering phase only draws columns that are partially visible and hence the width here
     * could be that between the extent of visible columns.
     * @return
     */
    public double getWidth() {
        return width;
    }

    /**
     * The ViewPort transformation.
     * @return
     */
    public Transform getTransform() {
        return transform;
    }

    /**
     * The Grid Widget itself.
     * @return
     */
    public BaseGridWidget<?, ?> getWidget() {
        return widget;
    }

}
