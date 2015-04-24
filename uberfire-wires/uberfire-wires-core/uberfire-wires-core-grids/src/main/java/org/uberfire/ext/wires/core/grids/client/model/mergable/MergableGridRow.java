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

import java.util.Stack;

import org.uberfire.ext.wires.core.grids.client.model.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.model.IGridCellValue;

/**
 * Implementation of a IGridRow in which cells can be merged.
 */
public class MergableGridRow extends BaseGridRow<MergableGridCell<?>> implements IMergableGridRow<MergableGridCell<?>> {

    private boolean hasMergedCells = false;
    private Stack<Double> heights = new Stack<Double>();
    private int collapseLevel = 0;

    public MergableGridRow() {
        this( 20 );
    }

    public MergableGridRow( final double height ) {
        this.height = height;
        this.heights.push( height );
    }

    @Override
    public boolean isMerged() {
        return hasMergedCells;
    }

    @Override
    public boolean isCollapsed() {
        return collapseLevel > 0;
    }

    @Override
    public void collapse() {
        collapseLevel++;
        heights.push( height );
        for ( MergableGridCell cell : cells.values() ) {
            cell.collapse();
        }
    }

    @Override
    public void expand() {
        if ( collapseLevel == 0 ) {
            return;
        }
        collapseLevel--;
        height = heights.pop();
        for ( MergableGridCell cell : cells.values() ) {
            cell.expand();
        }
    }

    @Override
    public void reset() {
        collapseLevel = 0;
        hasMergedCells = false;
        height = heights.firstElement();
        heights.clear();
        heights.push( height );
        for ( MergableGridCell cell : cells.values() ) {
            cell.reset();
        }
    }

    @Override
    public double peekHeight() {
        return heights.peek();
    }

    //This is not part of the IGridCell interface as we don't want to expose this for general use
    void setCell( final int columnIndex,
                  final IGridCellValue value ) {
        if ( !cells.containsKey( columnIndex ) ) {
            cells.put( columnIndex,
                       new MergableGridCell( value ) );
        } else {
            cells.get( columnIndex ).setValue( value );
        }
    }

    //This is not part of the IGridCell interface as we don't want to expose this for general use
    void deleteCell( final int columnIndex ) {
        cells.remove( columnIndex );
    }

    //This is not part of the IGridCell interface as we don't want to expose this for general use
    void setHasMergedCells( final boolean hasMergedCells ) {
        this.hasMergedCells = hasMergedCells;
    }

}
