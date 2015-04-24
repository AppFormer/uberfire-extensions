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
package org.uberfire.ext.wires.core.grids.client.model;

import java.util.List;

/**
 * An interface defining a generic grid of data.
 * @param <R> The generic type of rows within the grid
 * @param <C> The generic type of columns within the grid
 * @param <V> The generic type of cells within the grid
 */
public interface IGridData<R extends IGridRow<V>, C extends IGridColumn<R, V>, V extends IGridCell<?>> {

    /**
     * Get the columns associated with the grid.
     * @return
     */
    List<C> getColumns();

    /**
     * Append a column to the end of the grid. End being considered the far most right.
     * @param column
     */
    void appendColumn( final C column );

    /**
     * Insert a column to the grid at the specified index.
     * @param index
     * @param column
     */
    void insertColumn( final int index,
                       final C column );

    /**
     * Remove a column from the grid.
     * @param column
     */
    void removeColumn( final C column );

    /**
     * Move a column to a new index within the grid
     * @param index
     * @param column
     */
    void moveColumnTo( final int index,
                       final C column );

    /**
     * Get the x-coordinate of the column relative to the grid. i.e. 0 <= offset <= gridWidth.
     * @param gridColumn
     * @return
     */
    double getColumnOffset( final C gridColumn );

    /**
     * Get the x-coordinate of the column relative to the grid. i.e. 0 <= offset <= gridWidth.
     * @param columnIndex
     * @return
     */
    double getColumnOffset( final int columnIndex );

    /**
     * Get the y-coordinate of the row relative to the grid. i.e. 0 <= offset <= gridHeight.
     * @param gridRow
     * @return
     */
    double getRowOffset( final R gridRow );

    /**
     * Get the y-coordinate of the row relative to the grid. i.e. 0 <= offset <= gridHeight.
     * @param rowIndex
     * @return
     */
    double getRowOffset( final int rowIndex );

    /**
     * Append a row to the end of the grid.
     * @param row
     */
    void appendRow( final R row );

    /**
     * Insert a row to the grid at the specified index.
     * @param rowIndex
     * @param row
     */
    void insertRow( final int rowIndex,
                    final R row );

    /**
     * Get a row at the specified index.
     * @param rowIndex
     * @return
     */
    R getRow( final int rowIndex );

    /**
     * Get the total number of rows in the grid.
     * @return
     */
    int getRowCount();

    /**
     * Get a cell at the specified physical coordinate.
     * @param rowIndex
     * @param columnIndex
     * @return
     */
    V getCell( final int rowIndex,
               final int columnIndex );

    /**
     * Set a cell at the specified physical coordinate.
     * @param rowIndex
     * @param columnIndex
     * @param value
     */
    void setCell( final int rowIndex,
                  final int columnIndex,
                  final IGridCellValue<?> value );

    /**
     * Delete a cell at the specified physical coordinate.
     * @param rowIndex
     * @param columnIndex
     */
    void deleteCell( final int rowIndex,
                     final int columnIndex );

}
