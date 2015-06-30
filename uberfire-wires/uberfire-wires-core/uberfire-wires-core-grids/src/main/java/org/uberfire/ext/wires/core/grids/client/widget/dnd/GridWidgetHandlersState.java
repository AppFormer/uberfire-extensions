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

import com.google.gwt.dom.client.Style;
import org.uberfire.ext.wires.core.grids.client.model.IGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.IGridData;

/**
 * A container for the state of the MouseDown, MouseMove and MouseUp handlers during a drag operation.
 */
public class GridWidgetHandlersState {

    private IGridData grid = null;
    private IGridColumn gridColumn = null;
    private GridWidgetHandlersOperation operation = GridWidgetHandlersOperation.NONE;
    private Style.Cursor cursor = Style.Cursor.DEFAULT;

    private double eventInitialX = 0;
    private double eventInitialColumnWidth = 0;
    private GridWidgetColumnProxy eventColumnHighlight = new GridWidgetColumnProxy();

    /**
     * The different states of the drag operation.
     */
    public enum GridWidgetHandlersOperation {
        NONE,
        COLUMN_RESIZE_PENDING,
        COLUMN_RESIZE,
        COLUMN_MOVE_PENDING,
        COLUMN_MOVE
    }

    /**
     * The data backing the Grid.
     * @return
     */
    public IGridData getGrid() {
        return grid;
    }

    /**
     * Set the data backing the Grid.
     * @param grid
     */
    public void setGrid( final IGridData grid ) {
        this.grid = grid;
    }

    /**
     * The column being affected by the current the operation.
     * @return
     */
    public IGridColumn getGridColumn() {
        return gridColumn;
    }

    /**
     * Set the column to be affected by the current the operation.
     * @return
     */
    public void setGridColumn( final IGridColumn gridColumn ) {
        this.gridColumn = gridColumn;
    }

    /**
     * The current drag operation in progress.
     * @return
     */
    public GridWidgetHandlersOperation getOperation() {
        return operation;
    }

    /**
     * Set the current drag operation in progress.
     * @param operation
     */
    public void setOperation( final GridWidgetHandlersOperation operation ) {
        this.operation = operation;
    }

    /**
     * The Cursor type to be shown for the current operation. This primarily used in conjunction with DOMElement based cells.
     * When the pointer moves over a DOM element the browser determines the Cursor to show based on the DOM element's CSS. This
     * however can be different to the pointer required during, for example, a column resize operation. In such cases the
     * browser changes the pointer to that defined by CSS replacing that set by the MouseMove handler.
     * @return
     */
    public Style.Cursor getCursor() {
        return cursor;
    }

    /**
     * Set the Cursor type to be shown for the current operation.
     * @param cursor
     */
    public void setCursor( Style.Cursor cursor ) {
        this.cursor = cursor;
    }

    /**
     * @return
     */
    public double getEventInitialX() {
        return eventInitialX;
    }

    /**
     * Set the grid-relative x-coordinate of the Mouse Event.
     * @param eventInitialX
     */
    public void setEventInitialX( final double eventInitialX ) {
        this.eventInitialX = eventInitialX;
    }

    /**
     * The width of a column being re-sized at the commencement of the resize operation.
     * During a re-size operation the new width is determined by calculating the delta of
     * the MouseMoveEvent coordinates. The initial width is therefore required to apply
     * the same delta.
     * @return
     */
    public double getEventInitialColumnWidth() {
        return eventInitialColumnWidth;
    }

    /**
     * Set the initial width of a column to be resized.
     * @param eventInitialColumnWidth
     */
    public void setEventInitialColumnWidth( final double eventInitialColumnWidth ) {
        this.eventInitialColumnWidth = eventInitialColumnWidth;
    }

    /**
     * Get the Group representing the column during a drag operation of the column being moved
     * @return
     */
    public GridWidgetColumnProxy getEventColumnHighlight() {
        return eventColumnHighlight;
    }

}
