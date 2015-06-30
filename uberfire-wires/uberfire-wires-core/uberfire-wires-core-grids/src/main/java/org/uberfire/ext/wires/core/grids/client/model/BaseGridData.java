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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base implementation of a grid to avoid boiler-plate for more specific implementations.
 * @param <R> The generic type of rows within the grid
 * @param <C> The generic type of columns within the grid
 * @param <V> The generic type of cells within the grid
 */
public abstract class BaseGridData<R extends IGridRow<V>, C extends IGridColumn<R, V>, V extends IGridCell<?>> implements IGridData<R, C, V> {

    protected List<R> rows = new ArrayList<R>();
    protected List<C> columns = new ArrayList<C>();

    @Override
    public List<C> getColumns() {
        return Collections.unmodifiableList( columns );
    }

    @Override
    public void appendColumn( final C column ) {
        column.setIndex( columns.size() );
        columns.add( column );
    }

    @Override
    public void insertColumn( final int index,
                              final C column ) {
        column.setIndex( columns.size() );
        columns.add( index,
                     column );
    }

    @Override
    public void removeColumn( final C column ) {
        columns.remove( column );
        for ( R row : rows ) {
            row.getCells().remove( column.getIndex() );
        }
    }

    @Override
    public void moveColumnTo( final int index,
                              final C column ) {
        final int currentIndex = columns.indexOf( column );
        if ( index == currentIndex ) {
            return;
        }
        columns.remove( currentIndex );
        columns.add( index,
                     column );
    }

    @Override
    public double getColumnOffset( final C gridColumn ) {
        final int columnIndex = getColumns().indexOf( gridColumn );
        return getColumnOffset( columnIndex );
    }

    @Override
    public double getColumnOffset( final int columnIndex ) {
        double columnOffset = 0;
        final List<C> columns = getColumns();
        for ( int i = 0; i < columnIndex; i++ ) {
            final IGridColumn column = columns.get( i );
            if ( column.isVisible() ) {
                columnOffset = columnOffset + column.getWidth();
            }
        }
        return columnOffset;
    }

    @Override
    public double getRowOffset( final R gridRow ) {
        final int rowIndex = rows.indexOf( gridRow );
        return getRowOffset( rowIndex );
    }

    @Override
    public double getRowOffset( final int rowIndex ) {
        double height = 0;
        for ( int i = 0; i < rowIndex; i++ ) {
            final IGridRow<V> row = getRow( i );
            height = height + row.getHeight();
        }
        return height;
    }

    @Override
    public void appendRow( final R row ) {
        this.rows.add( row );
    }

    @Override
    public void insertRow( final int rowIndex,
                           final R row ) {
        this.rows.add( rowIndex,
                       row );
    }

    @Override
    public R getRow( final int rowIndex ) {
        return rows.get( rowIndex );
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public V getCell( final int rowIndex,
                      final int columnIndex ) {
        if ( rowIndex < 0 || rowIndex > rows.size() - 1 ) {
            return null;
        }
        final int _columnIndex = columns.get( columnIndex ).getIndex();
        return rows.get( rowIndex ).getCells().get( _columnIndex );
    }

}
