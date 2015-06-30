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
package org.uberfire.ext.wires.core.grids.client.model.basic;

import org.uberfire.ext.wires.core.grids.client.model.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.model.IGridCellValue;

/**
 * Implementation of a IGridRow in which cells cannot be merged.
 */
public class GridRow extends BaseGridRow<GridCell<?>> {

    //This is not part of the IGridCell interface as we don't want to expose this for general use
    void setCell( final int columnIndex,
                  final IGridCellValue value ) {
        if ( !cells.containsKey( columnIndex ) ) {
            cells.put( columnIndex,
                       new GridCell( value ) );
        } else {
            cells.get( columnIndex ).setValue( value );
        }
    }

    //This is not part of the IGridCell interface as we don't want to expose this for general use
    void deleteCell( final int columnIndex ) {
        cells.remove( columnIndex );
    }

}
