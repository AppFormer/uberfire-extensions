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
package org.uberfire.ext.wires.core.grids.client.widget.renderers.mergable;

import com.ait.lienzo.client.core.shape.Group;
import org.uberfire.ext.wires.core.grids.client.model.mergable.MergableGridData;
import org.uberfire.ext.wires.core.grids.client.widget.renderers.IGridRenderer;

/**
 * Extensions to the normal Renderer to support mergable data.
 */
public interface IMergableGridRenderer extends IGridRenderer<MergableGridData> {

    /**
     * Render to a Group a widget representing a merged cells' collapsed/expanded state.
     * @param cellWidth Width of the containing cell.
     * @param cellHeight Height of the containing cell.
     * @param isGrouped true is the cell is collapsed.
     * @return
     */
    Group renderGroupedCellToggle( final double cellWidth,
                                   final double cellHeight,
                                   final boolean isGrouped );

    /**
     * Render to a Group a widget representing merged cells containing different values.
     * @param cellWidth Width of the containing cell.
     * @param cellHeight Height of the containing cell.
     * @return
     */
    Group renderMergedCellMixedValueHighlight( final double cellWidth,
                                               final double cellHeight );

    /**
     * Check whether a cell-relative coordinate is "on" the hot-spot to toggle the collapsed/expanded state.
     * @param cellX The MouseEvent relative to the cell's x-coordinate.
     * @param cellY The MouseEvent relative to the cell's y-coordinate.
     * @param cellWidth Width of the containing cell.
     * @param cellHeight Height of the containing cell.
     * @return true if the cell coordinate is on the hot-spot.
     */
    boolean onGroupingToggle( final double cellX,
                              final double cellY,
                              final double cellWidth,
                              final double cellHeight );
}
