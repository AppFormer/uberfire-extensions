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

import org.uberfire.ext.wires.core.grids.client.model.BaseGridData;
import org.uberfire.ext.wires.core.grids.client.model.IGridCellValue;

/**
 * Implementation of IGridData that can contain merged cells.
 */
public class MergableGridData extends BaseGridData<MergableGridRow, MergableGridColumn<?>, MergableGridCell<?>> implements IMergableGridData {

    private boolean isMerged = true;

    @Override
    public boolean isMerged() {
        return this.isMerged;
    }

    @Override
    public void setMerged( final boolean isMerged ) {
        if ( this.isMerged == isMerged ) {
            return;
        }
        this.isMerged = isMerged;
        if ( isMerged ) {
            fullIndex();
        } else {
            reset();
        }
    }

    //Update all merge meta-data
    private void fullIndex() {
        for ( int columnIndex = 0; columnIndex < columns.size(); columnIndex++ ) {
            for ( int rowIndex = 0; rowIndex < rows.size(); rowIndex++ ) {
                final MergableGridCell currentRowCell = getCell( rowIndex,
                                                                 columnIndex );
                if ( currentRowCell == null ) {
                    continue;
                }

                currentRowCell.reset();

                int maxRowIndex = rowIndex + 1;
                while ( maxRowIndex < rows.size() ) {
                    final MergableGridCell nextRowCell = getCell( maxRowIndex,
                                                                  columnIndex );
                    if ( nextRowCell == null ) {
                        break;
                    }
                    if ( !nextRowCell.equals( currentRowCell ) ) {
                        break;
                    }
                    maxRowIndex++;
                }

                //Update merge meta-data
                if ( maxRowIndex - rowIndex > 1 ) {
                    for ( int i = rowIndex; i < maxRowIndex; i++ ) {
                        final MergableGridRow row = rows.get( i );
                        final MergableGridCell cell = getCell( i,
                                                               columnIndex );
                        cell.setMergedCellCount( 0 );
                        updateRowMergedCells( row );
                    }

                    final MergableGridRow row = rows.get( rowIndex );
                    final MergableGridCell cell = getCell( rowIndex,
                                                           columnIndex );
                    cell.setMergedCellCount( maxRowIndex - rowIndex );
                    updateRowMergedCells( row );

                    rowIndex = maxRowIndex - 1;
                }
            }
        }
    }

    //Clear all merge meta-data
    private void reset() {
        for ( MergableGridRow row : rows ) {
            row.reset();
        }
    }

    @Override
    public void setCell( final int rowIndex,
                         final int columnIndex,
                         final IGridCellValue<?> value ) {
        if ( rowIndex < 0 || rowIndex > rows.size() - 1 ) {
            return;
        }
        if ( columnIndex < 0 || columnIndex > columns.size() - 1 ) {
            return;
        }

        final int _columnIndex = columns.get( columnIndex ).getIndex();

        //If we're not merged just set the value of a single cell
        if ( !isMerged ) {
            rows.get( rowIndex ).setCell( _columnIndex,
                                          value );
            return;
        }

        //Find cell's current value
        int minRowIndex = findMinRowIndex( rowIndex,
                                           _columnIndex );
        int maxRowIndex = findMaxRowIndex( rowIndex,
                                           _columnIndex );

        //Update all rows' value
        for ( int i = minRowIndex; i < maxRowIndex; i++ ) {
            final MergableGridRow row = rows.get( i );
            row.setCell( _columnIndex,
                         value );
        }

        updateMergeMetaData( rowIndex,
                             _columnIndex );
    }

    @Override
    public void deleteCell( final int rowIndex,
                            final int columnIndex ) {
        if ( rowIndex < 0 || rowIndex > rows.size() - 1 ) {
            return;
        }
        if ( columnIndex < 0 || columnIndex > columns.size() - 1 ) {
            return;
        }

        final int _columnIndex = columns.get( columnIndex ).getIndex();

        //If we're not merged just set the value of a single cell
        if ( !isMerged ) {
            rows.get( rowIndex ).deleteCell( _columnIndex );
            return;
        }

        //Find cell's current value
        int minRowIndex = findMinRowIndex( rowIndex,
                                           _columnIndex );
        int maxRowIndex = findMaxRowIndex( rowIndex,
                                           _columnIndex );

        //Update all rows' value
        for ( int i = minRowIndex; i < maxRowIndex; i++ ) {
            final MergableGridRow row = rows.get( i );
            row.expand();
            row.deleteCell( _columnIndex );
            updateRowMergedCells( row );
        }

        updateMergeMetaData( rowIndex,
                             _columnIndex );
    }

    private int findMinRowIndex( final int rowIndex,
                                 final int columnIndex ) {
        int minRowIndex = rowIndex;
        final MergableGridRow currentRow = getRow( rowIndex );
        final MergableGridCell currentRowCell = currentRow.getCells().get( columnIndex );

        //Find minimum row with a cell containing the same value as that being updated
        boolean foundTopSplitMarker = currentRowCell == null ? false : currentRowCell.getMergedCellCount() > 0;
        while ( minRowIndex > 0 ) {
            final MergableGridRow previousRow = rows.get( minRowIndex - 1 );
            final MergableGridCell previousRowCell = previousRow.getCells().get( columnIndex );
            if ( previousRowCell == null ) {
                break;
            }
            if ( previousRowCell.isCollapsed() && foundTopSplitMarker ) {
                break;
            }
            if ( !previousRowCell.equals( currentRowCell ) ) {
                break;
            }
            if ( previousRowCell.getMergedCellCount() > 0 ) {
                foundTopSplitMarker = true;
            }
            minRowIndex--;
        }
        return minRowIndex;
    }

    private int findMaxRowIndex( final int rowIndex,
                                 final int columnIndex ) {
        int maxRowIndex = rowIndex + 1;
        final MergableGridRow currentRow = getRow( rowIndex );
        final MergableGridCell currentRowCell = currentRow.getCells().get( columnIndex );

        //Find maximum row with a cell containing the same value as that being updated
        boolean foundBottomSplitMarker = false;
        while ( maxRowIndex < rows.size() ) {
            final MergableGridRow nextRow = rows.get( maxRowIndex );
            final MergableGridCell nextRowCell = nextRow.getCells().get( columnIndex );
            if ( nextRowCell == null ) {
                break;
            }
            if ( nextRowCell.isCollapsed() && foundBottomSplitMarker ) {
                maxRowIndex--;
                break;
            }
            if ( !nextRowCell.equals( currentRowCell ) ) {
                break;
            }
            if ( nextRowCell.getMergedCellCount() > 0 ) {
                foundBottomSplitMarker = true;
            }
            maxRowIndex++;
        }
        return maxRowIndex;
    }

    @Override
    public void collapseCell( final int rowIndex,
                              final int columnIndex ) {
        //Data needs to be merged to collapse cells
        if ( !isMerged ) {
            return;
        }

        final int _columnIndex = columns.get( columnIndex ).getIndex();
        final MergableGridRow row = rows.get( rowIndex );
        final MergableGridCell cell = row.getCells().get( _columnIndex );
        if ( cell == null ) {
            return;
        }
        if ( !cell.isMerged() ) {
            return;
        }
        updateCollapseMetaDataOnCollapse( rowIndex,
                                          _columnIndex );
    }

    @Override
    public void expandCell( final int rowIndex,
                            final int columnIndex ) {
        //Data needs to be merged to expand cells
        if ( !isMerged ) {
            return;
        }

        final int _columnIndex = columns.get( columnIndex ).getIndex();
        final MergableGridRow row = rows.get( rowIndex );
        final MergableGridCell cell = row.getCells().get( _columnIndex );
        if ( cell == null ) {
            return;
        }
        updateCollapseMetaDataOnExpand( rowIndex,
                                        _columnIndex );
    }

    private void updateMergeMetaData( final int rowIndex,
                                      final int columnIndex ) {
        //Find cell's current value
        int minRowIndex = rowIndex;
        int maxRowIndex = rowIndex + 1;
        final MergableGridRow currentRow = getRow( rowIndex );
        final MergableGridCell currentRowCell = currentRow.getCells().get( columnIndex );

        if ( currentRowCell == null ) {
            return;
        }

        //Find minimum row with a cell containing the same value as that being updated
        boolean foundTopSplitMarker = currentRowCell.getMergedCellCount() > 0;
        while ( minRowIndex > 0 ) {
            final MergableGridRow previousRow = rows.get( minRowIndex - 1 );
            final MergableGridCell previousRowCell = previousRow.getCells().get( columnIndex );
            if ( previousRowCell == null ) {
                break;
            }
            if ( previousRowCell.isCollapsed() && foundTopSplitMarker ) {
                break;
            }
            if ( !previousRowCell.equals( currentRowCell ) ) {
                break;
            }
            if ( previousRowCell.getMergedCellCount() > 0 ) {
                foundTopSplitMarker = true;
            }
            minRowIndex--;
        }

        //Find maximum row with a cell containing the same value as that being updated
        boolean foundBottomSplitMarker = false;
        while ( maxRowIndex < rows.size() ) {
            final MergableGridRow nextRow = rows.get( maxRowIndex );
            final MergableGridCell nextRowCell = nextRow.getCells().get( columnIndex );
            if ( nextRowCell == null ) {
                break;
            }
            if ( nextRowCell.isCollapsed() && foundBottomSplitMarker ) {
                maxRowIndex--;
                break;
            }
            if ( !nextRowCell.equals( currentRowCell ) ) {
                break;
            }
            if ( nextRowCell.getMergedCellCount() > 0 ) {
                foundBottomSplitMarker = true;
            }
            maxRowIndex++;
        }

        //Update merge meta-data
        for ( int i = minRowIndex; i < maxRowIndex; i++ ) {
            final MergableGridRow row = rows.get( i );
            row.getCells().get( columnIndex ).setMergedCellCount( 0 );
            updateRowMergedCells( row );
        }

        final MergableGridRow row = rows.get( minRowIndex );
        row.getCells().get( columnIndex ).setMergedCellCount( maxRowIndex - minRowIndex );
        updateRowMergedCells( row );
    }

    private void updateRowMergedCells( final MergableGridRow row ) {
        for ( MergableGridCell cell : row.getCells().values() ) {
            if ( cell.isMerged() ) {
                row.setHasMergedCells( true );
                return;
            }
        }
        row.setHasMergedCells( false );
    }

    private void updateCollapseMetaDataOnCollapse( final int rowIndex,
                                                   final int columnIndex ) {
        int minRowIndex = rowIndex;
        int maxRowIndex = rowIndex + 1;
        final MergableGridRow currentRow = rows.get( rowIndex );
        final MergableGridCell currentRowCell = currentRow.getCells().get( columnIndex );

        if ( currentRowCell == null ) {
            return;
        }

        if ( currentRowCell.getMergedCellCount() == 0 ) {
            do {
                minRowIndex--;
                final MergableGridRow previousRow = rows.get( minRowIndex );
                final MergableGridCell previousRowCell = previousRow.getCells().get( columnIndex );
                if ( previousRowCell.getMergedCellCount() > 0 ) {
                    break;
                }
            }
            while ( minRowIndex > 0 );
        }

        while ( maxRowIndex < rows.size() ) {
            final MergableGridRow nextRow = rows.get( maxRowIndex );
            final MergableGridCell nextRowCell = nextRow.getCells().get( columnIndex );
            if ( nextRowCell == null ) {
                break;
            }
            if ( nextRowCell.getMergedCellCount() > 0 ) {
                break;
            }
            maxRowIndex++;
        }

        for ( int i = minRowIndex + 1; i < maxRowIndex; i++ ) {
            rows.get( i ).collapse();
        }

        for ( int i = 0; i < columns.size(); i++ ) {
            final int _columnIndex = columns.get( i ).getIndex();
            if ( _columnIndex == columnIndex ) {
                continue;
            }
            updateMergeMetaDataOnCollapseTopSplitRows( minRowIndex,
                                                       maxRowIndex,
                                                       _columnIndex );
            updateMergeMetaDataOnCollapseBottomSplitRows( minRowIndex,
                                                          maxRowIndex,
                                                          _columnIndex );
        }
    }

    private void updateMergeMetaDataOnCollapseTopSplitRows( final int minRowIndex,
                                                            final int maxRowIndex,
                                                            final int columnIndex ) {
        if ( minRowIndex < 1 ) {
            return;
        }

        final MergableGridRow checkTopRow = getRow( minRowIndex - 1 );
        final MergableGridCell checkTopCell = checkTopRow.getCells().get( columnIndex );

        if ( checkTopCell == null ) {
            return;
        }

        if ( checkTopCell.getMergedCellCount() == 1 ) {
            return;
        }

        // Scan from the first row before the start of collapsed block downwards to the end of the
        // collapsed block. If any cell is not identical to first then we need to split the cell.
        boolean splitTopSection = false;
        for ( int collapsedRowIndex = minRowIndex; collapsedRowIndex < maxRowIndex; collapsedRowIndex++ ) {
            final MergableGridRow collapsedRow = getRow( collapsedRowIndex );
            final MergableGridCell collapsedCell = collapsedRow.getCells().get( columnIndex );
            if ( collapsedCell == null ) {
                break;
            }
            if ( !collapsedCell.equals( checkTopCell ) ) {
                break;
            }
            splitTopSection = collapsedRowIndex < maxRowIndex - 1;
        }

        if ( splitTopSection ) {

            //Find minimum row with a cell containing the same value as the split-point
            int checkMinRowIndex = minRowIndex - 1;
            if ( checkTopCell.getMergedCellCount() == 0 ) {
                while ( checkMinRowIndex > 0 ) {
                    final MergableGridRow previousRow = rows.get( checkMinRowIndex );
                    final MergableGridCell previousRowCell = previousRow.getCells().get( columnIndex );
                    if ( previousRowCell.getMergedCellCount() > 0 ) {
                        break;
                    }
                    checkMinRowIndex--;
                }
            }

            //Update merge meta-data for top part of split cell
            if ( minRowIndex > checkMinRowIndex ) {
                for ( int i = checkMinRowIndex; i < minRowIndex; i++ ) {
                    final MergableGridRow row = rows.get( i );
                    row.getCells().get( columnIndex ).setMergedCellCount( 0 );
                    updateRowMergedCells( row );
                }

                final MergableGridRow topSplitRow = rows.get( checkMinRowIndex );
                topSplitRow.getCells().get( columnIndex ).setMergedCellCount( minRowIndex - checkMinRowIndex );
                updateRowMergedCells( topSplitRow );
            }

            //Find maximum row with a cell containing the same value as the split-point
            int checkMaxRowIndex = minRowIndex;
            boolean foundBottomSplitMarker = false;
            while ( checkMaxRowIndex < rows.size() ) {
                final MergableGridRow nextRow = rows.get( checkMaxRowIndex );
                final MergableGridCell nextRowCell = nextRow.getCells().get( columnIndex );
                if ( nextRowCell == null ) {
                    break;
                }
                if ( nextRowCell.isCollapsed() && foundBottomSplitMarker ) {
                    checkMaxRowIndex--;
                    break;
                }
                if ( !nextRowCell.equals( checkTopCell ) ) {
                    break;
                }
                if ( nextRowCell.getMergedCellCount() > 0 ) {
                    foundBottomSplitMarker = true;
                }
                checkMaxRowIndex++;
            }

            //Update merge meta-data for bottom part of split cell
            if ( checkMaxRowIndex > minRowIndex ) {
                for ( int i = minRowIndex; i < checkMaxRowIndex; i++ ) {
                    final MergableGridRow row = rows.get( i );
                    row.getCells().get( columnIndex ).setMergedCellCount( 0 );
                    updateRowMergedCells( row );
                }

                final MergableGridRow bottomSplitRow = rows.get( minRowIndex );
                bottomSplitRow.getCells().get( columnIndex ).setMergedCellCount( checkMaxRowIndex - minRowIndex );
                updateRowMergedCells( bottomSplitRow );
            }
        }
    }

    private void updateMergeMetaDataOnCollapseBottomSplitRows( final int minRowIndex,
                                                               final int maxRowIndex,
                                                               final int columnIndex ) {
        if ( maxRowIndex == rows.size() ) {
            return;
        }

        final MergableGridRow checkBottomRow = getRow( maxRowIndex );
        final MergableGridCell checkBottomCell = checkBottomRow.getCells().get( columnIndex );

        if ( checkBottomCell == null ) {
            return;
        }

        if ( checkBottomCell.getMergedCellCount() == 1 ) {
            return;
        }

        // Scan from the first row after the end of collapsed block upwards to the beginning of the
        // collapsed block. If any cell is not identical to first then we need to split the cell.
        boolean splitBottomSection = false;
        for ( int collapsedRowIndex = maxRowIndex - 1; collapsedRowIndex >= minRowIndex; collapsedRowIndex-- ) {
            final MergableGridRow collapsedRow = getRow( collapsedRowIndex );
            final MergableGridCell collapsedCell = collapsedRow.getCells().get( columnIndex );
            if ( collapsedCell == null ) {
                break;
            }
            if ( !collapsedCell.equals( checkBottomCell ) ) {
                break;
            }
            splitBottomSection = collapsedRowIndex > minRowIndex;
        }

        if ( splitBottomSection ) {

            //Find minimum row with a cell containing the same value as the split-point
            int checkMinRowIndex = maxRowIndex - 1;
            if ( checkBottomCell.getMergedCellCount() == 0 ) {
                while ( checkMinRowIndex > 0 ) {
                    final MergableGridRow previousRow = rows.get( checkMinRowIndex );
                    final MergableGridCell previousRowCell = previousRow.getCells().get( columnIndex );
                    if ( previousRowCell.getMergedCellCount() > 0 ) {
                        break;
                    }
                    checkMinRowIndex--;
                }
            }

            //Update merge meta-data for top part of split cell
            if ( maxRowIndex > checkMinRowIndex ) {
                for ( int i = checkMinRowIndex; i < maxRowIndex; i++ ) {
                    final MergableGridRow row = rows.get( i );
                    row.getCells().get( columnIndex ).setMergedCellCount( 0 );
                    updateRowMergedCells( row );
                }

                final MergableGridRow topSplitRow = rows.get( checkMinRowIndex );
                topSplitRow.getCells().get( columnIndex ).setMergedCellCount( maxRowIndex - checkMinRowIndex );
                updateRowMergedCells( topSplitRow );
            }

            //Find maximum row with a cell containing the same value as the split-point
            int checkMaxRowIndex = maxRowIndex;
            boolean foundBottomSplitMarker = false;
            while ( checkMaxRowIndex < rows.size() ) {
                final MergableGridRow nextRow = rows.get( checkMaxRowIndex );
                final MergableGridCell nextRowCell = nextRow.getCells().get( columnIndex );
                if ( nextRowCell == null ) {
                    break;
                }
                if ( nextRowCell.isCollapsed() && foundBottomSplitMarker ) {
                    checkMaxRowIndex--;
                    break;
                }
                if ( !nextRowCell.equals( checkBottomCell ) ) {
                    break;
                }
                if ( nextRowCell.getMergedCellCount() > 0 ) {
                    foundBottomSplitMarker = true;
                }
                checkMaxRowIndex++;
            }

            //Update merge meta-data for bottom part of split cell
            if ( checkMaxRowIndex > maxRowIndex ) {
                for ( int i = maxRowIndex; i < checkMaxRowIndex; i++ ) {
                    final MergableGridRow row = rows.get( i );
                    row.getCells().get( columnIndex ).setMergedCellCount( 0 );
                    updateRowMergedCells( row );
                }

                //Only split bottom if it isn't already split
                final MergableGridRow bottomSplitRow = rows.get( maxRowIndex );
                if ( bottomSplitRow.getCells().get( columnIndex ).getMergedCellCount() == 0 ) {
                    bottomSplitRow.getCells().get( columnIndex ).setMergedCellCount( checkMaxRowIndex - maxRowIndex );
                    updateRowMergedCells( bottomSplitRow );
                }
            }
        }
    }

    private void updateCollapseMetaDataOnExpand( final int rowIndex,
                                                 final int columnIndex ) {
        int minRowIndex = rowIndex;
        int maxRowIndex = rowIndex + 1;
        final MergableGridRow currentRow = rows.get( rowIndex );
        final MergableGridCell currentRowCell = currentRow.getCells().get( columnIndex );

        if ( currentRowCell == null ) {
            return;
        }

        if ( currentRowCell.getMergedCellCount() == 0 ) {
            do {
                minRowIndex--;
                final MergableGridRow previousRow = rows.get( minRowIndex );
                final MergableGridCell previousRowCell = previousRow.getCells().get( columnIndex );
                if ( previousRowCell.getMergedCellCount() > 0 ) {
                    break;
                }
            }
            while ( minRowIndex > 0 );
        }

        while ( maxRowIndex < rows.size() ) {
            final MergableGridRow nextRow = rows.get( maxRowIndex );
            final MergableGridCell nextRowCell = nextRow.getCells().get( columnIndex );
            if ( nextRowCell == null ) {
                break;
            }
            if ( nextRowCell.getMergedCellCount() > 0 ) {
                break;
            }
            maxRowIndex++;
        }

        for ( int i = minRowIndex + 1; i < maxRowIndex; i++ ) {
            rows.get( i ).expand();
        }

        for ( int i = 0; i < getColumns().size(); i++ ) {
            final int _columnIndex = getColumns().get( i ).getIndex();
            updateMergeMetaDataOnExpand( minRowIndex,
                                         _columnIndex,
                                         minRowIndex,
                                         maxRowIndex );
            updateMergeMetaDataOnExpand( maxRowIndex - 1,
                                         _columnIndex,
                                         minRowIndex,
                                         maxRowIndex );
        }
    }

    private void updateMergeMetaDataOnExpand( final int rowIndex,
                                              final int columnIndex,
                                              final int expandMinRowIndex,
                                              final int expandMaxRowIndex ) {
        //Find cell's current value
        int minRowIndex = rowIndex;
        int maxRowIndex = rowIndex + 1;
        final MergableGridRow currentRow = getRow( rowIndex );
        final MergableGridCell currentRowCell = currentRow.getCells().get( columnIndex );

        if ( currentRowCell == null ) {
            return;
        }

        //Find minimum row with a cell containing the same value as that being updated
        boolean foundTopSplitMarker = currentRowCell.getMergedCellCount() > 0;
        while ( minRowIndex > 0 ) {
            final MergableGridRow previousRow = rows.get( minRowIndex - 1 );
            final MergableGridCell previousRowCell = previousRow.getCells().get( columnIndex );
            if ( previousRowCell == null ) {
                break;
            }
            if ( previousRowCell.isCollapsed() && foundTopSplitMarker ) {
                break;
            }
            if ( !previousRowCell.equals( currentRowCell ) ) {
                break;
            }
            if ( previousRowCell.getMergedCellCount() > 0 ) {
                foundTopSplitMarker = true;
            }
            minRowIndex--;
        }

        //Find maximum row with a cell containing the same value as that being updated
        boolean foundBottomSplitMarker = false;
        while ( maxRowIndex < rows.size() ) {
            final MergableGridRow nextRow = rows.get( maxRowIndex );
            final MergableGridCell nextRowCell = nextRow.getCells().get( columnIndex );
            if ( nextRowCell == null ) {
                break;
            }
            if ( nextRowCell.isCollapsed() && foundBottomSplitMarker ) {
                maxRowIndex--;
                break;
            }
            if ( !nextRowCell.equals( currentRowCell ) ) {
                break;
            }
            if ( nextRowCell.getMergedCellCount() > 0 ) {
                foundBottomSplitMarker = true;
            }
            maxRowIndex++;
        }

        //Update merge meta-data
        for ( int i = minRowIndex; i < maxRowIndex; i++ ) {
            final MergableGridRow row = rows.get( i );
            row.getCells().get( columnIndex ).setMergedCellCount( 0 );
            updateRowMergedCells( row );
        }

        final MergableGridRow row = rows.get( minRowIndex );
        row.getCells().get( columnIndex ).setMergedCellCount( maxRowIndex - minRowIndex );
        updateRowMergedCells( row );

        //If merged block is partially collapsed split it
        if ( maxRowIndex > expandMaxRowIndex ) {
            final MergableGridRow bottomSplitRow = rows.get( expandMaxRowIndex );
            if ( bottomSplitRow.isCollapsed() ) {
                bottomSplitRow.getCells().get( columnIndex ).setMergedCellCount( maxRowIndex - expandMaxRowIndex );
                updateRowMergedCells( bottomSplitRow );
                bottomSplitRow.expand();
                row.getCells().get( columnIndex ).setMergedCellCount( expandMaxRowIndex - minRowIndex );
                updateRowMergedCells( row );
            }
        }
    }

}
