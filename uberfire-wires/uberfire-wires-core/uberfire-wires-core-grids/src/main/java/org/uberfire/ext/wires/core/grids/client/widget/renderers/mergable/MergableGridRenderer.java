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

import java.util.ArrayList;
import java.util.List;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.shared.core.types.ColorName;
import org.uberfire.ext.wires.core.grids.client.model.mergable.MergableGridCell;
import org.uberfire.ext.wires.core.grids.client.model.mergable.MergableGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.mergable.MergableGridData;
import org.uberfire.ext.wires.core.grids.client.model.mergable.MergableGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.BaseGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderRenderContext;

/**
 * A renderer that only renders the visible columns and rows of merged data. This implementation
 * can render the data either in a merged state or non-merged state.
 */
public class MergableGridRenderer implements IMergableGridRenderer {

    private static final int HEADER_HEIGHT = 34;
    private static final MergableGridRendererMerged mergedRenderer = new MergableGridRendererMerged();
    private static final MergableGridRendererFlattened flattenedRenderer = new MergableGridRendererFlattened();

    @Override
    public String getName() {
        return "Mergable";
    }

    @Override
    public double getHeaderHeight() {
        return HEADER_HEIGHT;
    }

    @Override
    public Group renderSelector( final double width,
                                 final double height ) {
        final Group g = new Group();
        final Rectangle r = new Rectangle( width,
                                           height )
                .setStrokeWidth( 2.0 )
                .setStrokeColor( ColorName.GREEN )
                .setShadow( new Shadow( ColorName.DARKGREEN, 4, 0.0, 0.0 ) )
                .setListening( false );
        g.add( r );
        return g;
    }

    @Override
    public Group renderHeader( final MergableGridData model,
                               final GridHeaderRenderContext context ) {
        final int startColumnIndex = context.getStartColumnIndex();
        final int endColumnIndex = context.getEndColumnIndex();
        final double width = context.getWidth();

        final Group g = new Group();
        final Rectangle header = new Rectangle( 0, 0 )
                .setFillColor( ColorName.BISQUE )
                .setStrokeColor( ColorName.GREY )
                .setStrokeWidth( 0.5 )
                .setWidth( width )
                .setHeight( HEADER_HEIGHT );
        g.add( header );

        final List<MergableGridColumn<?>> columns = model.getColumns();

        //Linked columns
        double x = 0;
        for ( int i = startColumnIndex; i <= endColumnIndex; i++ ) {
            final MergableGridColumn column = columns.get( i );
            if ( column.isVisible() ) {
                final double w = column.getWidth();
                if ( column.isLinked() ) {
                    final Rectangle lr = new Rectangle( w,
                                                        HEADER_HEIGHT )
                            .setFillColor( ColorName.BROWN )
                            .setStrokeColor( ColorName.GREY )
                            .setStrokeWidth( 0.5 )
                            .setX( x );
                    g.add( lr );
                }
                x = x + w;
            }
        }

        //Grid lines
        x = 0;
        final MultiPath headerGrid = new MultiPath()
                .setStrokeColor( ColorName.GREY )
                .setStrokeWidth( 0.5 )
                .setListening( false );
        for ( int i = startColumnIndex; i <= endColumnIndex; i++ ) {
            final MergableGridColumn column = columns.get( i );
            if ( column.isVisible() ) {
                headerGrid.M( x, 0 ).L( x,
                                        HEADER_HEIGHT );
                x = x + column.getWidth();
            }
        }
        g.add( headerGrid );

        final MultiPath headerDivider = new MultiPath()
                .setStrokeColor( ColorName.GREY )
                .setStrokeWidth( 1.0 )
                .setListening( false );
        headerDivider.M( 0,
                         HEADER_HEIGHT - 4 )
                .L( x,
                    HEADER_HEIGHT - 4 );
        g.add( headerDivider );

        //Column text
        x = 0;
        for ( int i = startColumnIndex; i <= endColumnIndex; i++ ) {
            final MergableGridColumn column = columns.get( i );
            if ( column.isVisible() ) {
                final Group hc = column.renderHeader();
                final double w = column.getWidth();
                hc.setX( x + w / 2 )
                        .setY( getHeaderHeight() / 2 )
                        .setListening( false );
                g.add( hc );
                x = x + w;
            }
        }

        return g;
    }

    @Override
    public Group renderBody( final MergableGridData model,
                             final GridBodyRenderContext context ) {
        //Delegate to one of the inner class implementations that support (un)merged rendering
        if ( model.isMerged() ) {
            return mergedRenderer.renderBody( model,
                                              context );
        } else {
            return flattenedRenderer.renderBody( model,
                                                 context );
        }
    }

    protected Rectangle getBodyBackground() {
        final Rectangle body = new Rectangle( 0, 0 )
                .setFillColor( ColorName.LIGHTYELLOW )
                .setStrokeColor( ColorName.SLATEGRAY )
                .setStrokeWidth( 0.5 );
        return body;
    }

    public MultiPath getBodyGridLine() {
        final MultiPath bodyGrid = new MultiPath()
                .setStrokeColor( ColorName.DARKGRAY )
                .setStrokeWidth( 0.5 )
                .setListening( false );
        return bodyGrid;
    }

    @Override
    public Group renderGroupedCellToggle( final double cellWidth,
                                          final double cellHeight,
                                          final boolean isCollapsed ) {
        return new GroupingToggle( cellWidth,
                                   cellHeight,
                                   isCollapsed );
    }

    @Override
    public Group renderMergedCellMixedValueHighlight( final double cellWidth,
                                                      final double cellHeight ) {
        final Group g = new Group();
        final Rectangle multiValueHighlight = new Rectangle( cellWidth,
                                                             cellHeight );
        multiValueHighlight.setFillColor( ColorName.GOLDENROD );
        g.add( multiValueHighlight );
        return g;
    }

    @Override
    public boolean onGroupingToggle( double cellX,
                                     double cellY,
                                     double cellWidth,
                                     double cellHeight ) {
        return GroupingToggle.onHotSpot( cellX,
                                         cellY,
                                         cellWidth,
                                         cellHeight );
    }

    /**
     * Inner class implementation that supports merged and collapsed cells
     */
    private static class MergableGridRendererMerged extends MergableGridRenderer {

        @Override
        public Group renderBody( final MergableGridData model,
                                 final GridBodyRenderContext context ) {
            final int startColumnIndex = context.getStartColumnIndex();
            final int endColumnIndex = context.getEndColumnIndex();
            final int startRowIndex = context.getStartRowIndex();
            final int endRowIndex = context.getEndRowIndex();
            final double width = context.getWidth();
            final Transform transform = context.getTransform();
            final BaseGridWidget<?, ?> widget = context.getWidget();

            final Group g = new Group();
            final List<MergableGridColumn<?>> columns = model.getColumns();
            final List<Double> rowOffsets = new ArrayList<Double>();
            double rowOffset = model.getRowOffset( startRowIndex );
            for ( int rowIndex = startRowIndex; rowIndex <= endRowIndex; rowIndex++ ) {
                rowOffsets.add( rowOffset );
                rowOffset = rowOffset + model.getRow( rowIndex ).getHeight();
            }

            final double maxY = rowOffsets.get( endRowIndex - startRowIndex ) - rowOffsets.get( 0 ) + model.getRow( endRowIndex ).getHeight();
            final double maxX = model.getColumnOffset( endColumnIndex ) - model.getColumnOffset( startColumnIndex ) + columns.get( endColumnIndex ).getWidth();
            final Rectangle body = getBodyBackground().setWidth( width ).setHeight( maxY );
            g.add( body );

            //Grid lines - Verticals - easy!
            final MultiPath bodyGrid = getBodyGridLine();
            double x = 0;
            for ( int i = startColumnIndex; i <= endColumnIndex; i++ ) {
                final MergableGridColumn column = columns.get( i );
                if ( column.isVisible() ) {
                    bodyGrid.M( x,
                                0 ).L( x,
                                       maxY );
                    x = x + column.getWidth();
                }
            }

            //Grid lines - Horizontals - not so easy!
            for ( int rowIndex = startRowIndex; rowIndex <= endRowIndex; rowIndex++ ) {
                x = 0;
                final double y = rowOffsets.get( rowIndex - startRowIndex ) - rowOffsets.get( 0 );
                final MergableGridRow row = model.getRow( rowIndex );

                if ( !row.isMerged() ) {
                    //If row doesn't contain merged cells just draw a line across the visible body
                    bodyGrid.M( x,
                                y ).L( maxX,
                                       y );

                } else if ( !row.isCollapsed() ) {
                    //We need to break the line into sections for the different cells
                    for ( int columnIndex = startColumnIndex; columnIndex <= endColumnIndex; columnIndex++ ) {
                        final MergableGridColumn column = columns.get( columnIndex );
                        final MergableGridCell cell = model.getCell( rowIndex,
                                                                     columnIndex );

                        if ( cell == null || cell.getMergedCellCount() > 0 ) {
                            //Draw a line-segment for empty cells and cells that are to have content rendered
                            bodyGrid.M( x,
                                        y ).L( x + column.getWidth(),
                                               y );

                        } else if ( isCollapsedRowMultiValue( model,
                                                              column,
                                                              cell,
                                                              rowIndex ) ) {
                            //Special case for when a cell follows collapsed row(s) with multiple values
                            bodyGrid.M( x,
                                        y ).L( x + column.getWidth(),
                                               y );
                        }
                        x = x + column.getWidth();
                    }
                }
            }
            g.add( bodyGrid );

            //Cell content
            x = 0;
            for ( int columnIndex = startColumnIndex; columnIndex <= endColumnIndex; columnIndex++ ) {
                final MergableGridColumn column = columns.get( columnIndex );
                if ( column.isVisible() ) {
                    final double columnWidth = column.getWidth();
                    for ( int rowIndex = startRowIndex; rowIndex <= endRowIndex; rowIndex++ ) {
                        final double y = rowOffsets.get( rowIndex - startRowIndex ) - rowOffsets.get( 0 );
                        final MergableGridRow row = model.getRow( rowIndex );
                        final MergableGridCell cell = model.getCell( rowIndex,
                                                                     columnIndex );

                        //Only show content for rows that are not collapsed
                        if ( !row.isCollapsed() ) {

                            //Add highlight for merged cells with different values
                            final boolean isCollapsedCellMixedValue = isCollapsedCellMixedValue( model,
                                                                                                 rowIndex,
                                                                                                 columnIndex );

                            if ( isCollapsedCellMixedValue ) {
                                final Group mixedValueGroup = renderMergedCellMixedValueHighlight( columnWidth,
                                                                                                   row.getHeight() );
                                mixedValueGroup.setX( x )
                                        .setY( y )
                                        .setListening( false );
                                g.add( mixedValueGroup );
                            }

                            //Only show content if there's a Cell behind it!
                            if ( cell != null ) {

                                //Add Group Toggle for first row in a Merged block
                                if ( cell.getMergedCellCount() > 1 ) {
                                    final MergableGridCell nextRowCell = model.getCell( rowIndex + 1,
                                                                                        columnIndex );
                                    if ( nextRowCell != null ) {
                                        final Group gt = renderGroupedCellToggle( columnWidth,
                                                                                  row.getHeight(),
                                                                                  nextRowCell.isCollapsed() );
                                        gt.setX( x ).setY( y );
                                        g.add( gt );
                                    }
                                }

                                if ( cell.getMergedCellCount() > 0 ) {
                                    //If cell is "lead" i.e. top of a merged block centralize content in cell
                                    final double cellHeight = getCellHeight( rowIndex,
                                                                             model,
                                                                             cell );
                                    final GridCellRenderContext cellContext = new GridCellRenderContext( context.getX() + model.getColumnOffset( columnIndex ),
                                                                                                         context.getY() + getHeaderHeight() + rowOffsets.get( rowIndex - startRowIndex ),
                                                                                                         columnWidth,
                                                                                                         cellHeight,
                                                                                                         rowIndex,
                                                                                                         columnIndex,
                                                                                                         transform,
                                                                                                         widget );
                                    final Group hc = column.renderCell( row,
                                                                        cellContext );
                                    hc.setX( x )
                                            .setY( y )
                                            .setListening( false );
                                    g.add( hc );

                                    //Skip remainder of merged block
                                    rowIndex = rowIndex + cell.getMergedCellCount() - 1;

                                } else {
                                    //Otherwise the cell has been clipped and we need to back-track to the "lead" cell to centralize content
                                    double _y = y;
                                    int _rowIndex = rowIndex;
                                    MergableGridCell _cell = cell;
                                    while ( _cell.getMergedCellCount() == 0 ) {
                                        _rowIndex--;
                                        _y = _y - model.getRow( _rowIndex ).getHeight();
                                        _cell = model.getCell( _rowIndex,
                                                               columnIndex );
                                    }

                                    final double cellHeight = getCellHeight( _rowIndex,
                                                                             model,
                                                                             _cell );
                                    final GridCellRenderContext cellContext = new GridCellRenderContext( context.getX() + model.getColumnOffset( columnIndex ),
                                                                                                         context.getY() + getHeaderHeight() + model.getRowOffset( _rowIndex ),
                                                                                                         columnWidth,
                                                                                                         cellHeight,
                                                                                                         rowIndex,
                                                                                                         columnIndex,
                                                                                                         transform,
                                                                                                         widget );

                                    final Group hc = column.renderCell( row,
                                                                        cellContext );
                                    hc.setX( x )
                                            .setY( _y )
                                            .setListening( false );
                                    g.add( hc );

                                    //Skip remainder of merged block
                                    rowIndex = _rowIndex + _cell.getMergedCellCount() - 1;
                                }
                            }

                        }
                    }
                    x = x + columnWidth;
                }
            }

            return g;
        }

        private double getCellHeight( final int rowIndex,
                                      final MergableGridData model,
                                      final MergableGridCell cell ) {
            double height = 0;
            for ( int i = rowIndex; i < rowIndex + cell.getMergedCellCount(); i++ ) {
                height = height + model.getRow( i ).getHeight();
            }
            return height;
        }

        private boolean isCollapsedRowMultiValue( final MergableGridData model,
                                                  final MergableGridColumn column,
                                                  final MergableGridCell cell,
                                                  final int rowIndex ) {
            MergableGridRow row;
            int rowOffset = 1;
            final int columnIndex = column.getIndex();

            //Iterate collapsed rows checking if the values differ
            while ( ( row = model.getRow( rowIndex - rowOffset ) ).isCollapsed() ) {
                final MergableGridCell nc = row.getCells().get( columnIndex );
                if ( nc == null ) {
                    return true;
                }
                if ( !cell.getValue().equals( nc.getValue() ) ) {
                    return true;
                }
                rowOffset++;
            }

            //Check "lead" row as well - since this is not marked as collapsed
            final MergableGridCell nc = row.getCells().get( columnIndex );
            if ( nc == null ) {
                return true;
            }
            if ( !cell.getValue().equals( nc.getValue() ) ) {
                return true;
            }
            return false;
        }

        private boolean isCollapsedCellMixedValue( final MergableGridData model,
                                                   final int rowIndex,
                                                   final int columnIndex ) {
            int _rowIndex = rowIndex;
            MergableGridCell currentCell = model.getCell( _rowIndex,
                                                          columnIndex );
            if ( currentCell != null ) {
                while ( _rowIndex > 0 && currentCell.getMergedCellCount() == 0 ) {
                    _rowIndex--;
                    currentCell = model.getCell( _rowIndex,
                                                 columnIndex );
                }
            }

            _rowIndex++;
            if ( _rowIndex > model.getRowCount() - 1 ) {
                return false;
            }
            while ( _rowIndex < model.getRowCount() && model.getRow( _rowIndex ).isCollapsed() ) {
                final MergableGridCell nextCell = model.getCell( _rowIndex,
                                                                 columnIndex );
                if ( currentCell == null ) {
                    if ( nextCell != null ) {
                        return true;
                    }
                } else {
                    if ( nextCell == null ) {
                        return true;
                    }
                    if ( !currentCell.getValue().getValue().equals( nextCell.getValue().getValue() ) ) {
                        return true;
                    }
                }
                _rowIndex++;
            }

            return false;
        }

    }

    /**
     * Inner class implementation that does NOT support merged and collapsed cells
     */
    private static class MergableGridRendererFlattened extends MergableGridRenderer {

        @Override
        public Group renderBody( final MergableGridData model,
                                 final GridBodyRenderContext context ) {
            final int startColumnIndex = context.getStartColumnIndex();
            final int endColumnIndex = context.getEndColumnIndex();
            final int startRowIndex = context.getStartRowIndex();
            final int endRowIndex = context.getEndRowIndex();
            final double width = context.getWidth();
            final Transform transform = context.getTransform();
            final BaseGridWidget<?, ?> widget = context.getWidget();

            final Group g = new Group();
            final List<MergableGridColumn<?>> columns = model.getColumns();

            final List<Double> rowOffsets = new ArrayList<Double>();
            double rowOffset = model.getRowOffset( startRowIndex );
            for ( int rowIndex = startRowIndex; rowIndex <= endRowIndex; rowIndex++ ) {
                rowOffsets.add( rowOffset );
                rowOffset = rowOffset + model.getRow( rowIndex ).getHeight();
            }

            final double maxY = rowOffsets.get( endRowIndex - startRowIndex ) - rowOffsets.get( 0 ) + model.getRow( endRowIndex ).getHeight();
            final double maxX = model.getColumnOffset( endColumnIndex ) - model.getColumnOffset( startColumnIndex ) + columns.get( endColumnIndex ).getWidth();
            final Rectangle body = getBodyBackground().setWidth( width ).setHeight( maxY );
            g.add( body );

            //Grid lines
            final MultiPath bodyGrid = getBodyGridLine();
            double x = 0;
            for ( int i = startColumnIndex; i <= endColumnIndex; i++ ) {
                final MergableGridColumn column = columns.get( i );
                if ( column.isVisible() ) {
                    bodyGrid.M( x,
                                0 ).L( x,
                                       maxY );
                    x = x + column.getWidth();
                }
            }
            for ( int rowIndex = startRowIndex; rowIndex <= endRowIndex; rowIndex++ ) {
                final double y = rowOffsets.get( rowIndex - startRowIndex ) - rowOffsets.get( 0 );
                bodyGrid.M( 0,
                            y ).L( maxX,
                                   y );
            }
            g.add( bodyGrid );

            //Cell content
            for ( int rowIndex = startRowIndex; rowIndex <= endRowIndex; rowIndex++ ) {
                final double y = rowOffsets.get( rowIndex - startRowIndex ) - rowOffsets.get( 0 );
                final MergableGridRow row = model.getRow( rowIndex );
                x = 0;
                for ( int columnIndex = startColumnIndex; columnIndex <= endColumnIndex; columnIndex++ ) {
                    final MergableGridColumn column = columns.get( columnIndex );
                    if ( column.isVisible() ) {
                        final double rowHeight = row.getHeight();
                        final double columnWidth = column.getWidth();
                        final GridCellRenderContext cellContext = new GridCellRenderContext( context.getX() + model.getColumnOffset( columnIndex ),
                                                                                             context.getY() + getHeaderHeight() + rowOffsets.get( rowIndex - startRowIndex ),
                                                                                             columnWidth,
                                                                                             rowHeight,
                                                                                             rowIndex,
                                                                                             columnIndex,
                                                                                             transform,
                                                                                             widget );
                        final Group hc = column.renderCell( row,
                                                            cellContext );
                        if ( hc != null ) {
                            hc.setX( x )
                                    .setY( y )
                                    .setListening( false );
                            g.add( hc );
                        }
                        x = x + columnWidth;
                    }
                }
            }

            return g;
        }

    }

}
