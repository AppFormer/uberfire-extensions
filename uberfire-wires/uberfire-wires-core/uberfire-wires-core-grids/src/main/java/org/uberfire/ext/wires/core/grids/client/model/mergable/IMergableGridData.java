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

import org.uberfire.ext.wires.core.grids.client.model.IGridData;

public interface IMergableGridData extends IGridData<MergableGridRow, MergableGridColumn<?>, MergableGridCell<?>> {

    /**
     * Whether the data in a merged state.
     * @return true if merged.
     */
    boolean isMerged();

    /**
     * Set whether the data is in merged state.
     * @param isMerged
     */
    void setMerged( final boolean isMerged );

    /**
     * Collapse a cell and corresponding rows. The cell being collapsed has all other merged
     * cells below it collapsed into the single cell. The cell itself remains not collapsed.
     * @param rowIndex
     * @param columnIndex
     */
    void collapseCell( final int rowIndex,
                       final int columnIndex );

    /**
     * Expand a cell and corresponding rows. The cell being collapsed has all other merged cells
     * below it expanded. Expanding collapsed cells should not expand nested collapsed cells.
     * @param rowIndex
     * @param columnIndex
     */
    void expandCell( final int rowIndex,
                     final int columnIndex );

}
