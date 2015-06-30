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
 * The context of a Grid's cell during the rendering phase.
 */
public class GridCellRenderContext {

    private final double x;
    private final double y;
    private final double width;
    private final double height;
    private final int rowIndex;
    private final int columnIndex;
    private final Transform transform;
    private final BaseGridWidget<?, ?> widget;

    public GridCellRenderContext( final double x,
                                  final double y,
                                  final double width,
                                  final double height,
                                  final int rowIndex,
                                  final int columnIndex,
                                  final Transform transform,
                                  final BaseGridWidget<?, ?> widget ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
        this.transform = transform;
        this.widget = widget;
    }

    /**
     * The cell's canvas x-coordinate; not transformed.
     * @return
     */
    public double getX() {
        return x;
    }

    /**
     * The cell's canvas y-coordinate; not transformed.
     * @return
     */
    public double getY() {
        return y;
    }

    /**
     * The width of the cell.
     * @return
     */
    public double getWidth() {
        return width;
    }

    /**
     * The height of the cell.
     * @return
     */
    public double getHeight() {
        return height;
    }

    /**
     * The index of the row this cell represents.
     * @return
     */
    public int getRowIndex() {
        return rowIndex;
    }

    /**
     * The index of the column this cell represents.
     * @return
     */
    public int getColumnIndex() {
        return columnIndex;
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
