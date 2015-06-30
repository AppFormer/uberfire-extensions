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
package org.uberfire.ext.wires.core.grids.client.model.mergable;

import org.uberfire.ext.wires.core.grids.client.model.IGridCell;

/**
 * Extension to a regular non-merged grid to support merged cells.
 * @param <T> The Type of value
 */
public interface IMergableGridCell<T> extends IGridCell<T> {

    /**
     * Whether the cell in a merged state
     * @return true if merged
     */
    boolean isMerged();

    /**
     * Return the number of cells merged into this cell. For cells that are at the top
     * of a merged block this should be the number of merged cells, including this cell.
     * For cells that are not the top of a merged block but are contained in a merged
     * block this should return zero. For non-merged cells this should return one.
     * @return The number of cells merged into this cell, or zero if part of a merged block or one if not merged.
     */
    int getMergedCellCount();

    /**
     * Whether the cell is collapsed. For cells that are at the top of a collapsed
     * block this should return false. For cells that are not the top of a collapsed block
     * but are contained in a collapsed block this should return false.
     * @return true is collapsed.
     */
    boolean isCollapsed();

    /**
     * Collapse the cell.
     */
    void collapse();

    /**
     * Expand the cell.
     */
    void expand();

    /**
     * Reset the cell to a non-merged, non-collapsed state.
     */
    void reset();

}
