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

import org.uberfire.ext.wires.core.grids.client.model.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.IGridCellValue;

/**
 * Implementation of IGridData that cannot contain merged cells.
 */
public class GridData extends BaseGridData<GridRow, GridColumn<?>, GridCell<?>> {

    @Override
    public void setCell( final int rowIndex,
                         final int columnIndex,
                         final IGridCellValue<?> value ) {
        if ( rowIndex < 0 || rowIndex > rows.size() - 1 ) {
            return;
        }
        final GridRow row = rows.get( rowIndex );
        final int _columnIndex = columns.get( columnIndex ).getIndex();
        row.setCell( _columnIndex,
                     value );
    }

    @Override
    public void deleteCell( final int rowIndex,
                            final int columnIndex ) {
        if ( rowIndex < 0 || rowIndex > rows.size() - 1 ) {
            return;
        }
        final GridRow row = rows.get( rowIndex );
        final int _columnIndex = columns.get( columnIndex ).getIndex();
        row.deleteCell( _columnIndex );
    }

}
