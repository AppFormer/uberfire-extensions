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
package org.uberfire.ext.wires.core.grids.client.widget.renderers.basic;

import java.util.ArrayList;
import java.util.List;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.types.Transform;
import org.uberfire.ext.wires.core.grids.client.model.basic.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.basic.GridData;
import org.uberfire.ext.wires.core.grids.client.model.basic.GridRow;
import org.uberfire.ext.wires.core.grids.client.widget.BaseGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridHeaderRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.renderers.IGridRenderer;

/**
 * A base renderer that only renders the visible columns and rows of non-merged data.
 */
public abstract class AbstractClippingGridRenderer implements IGridRenderer<GridData> {

    @Override
    public Group renderSelector( final double width,
                                 final double height ) {
        final Group g = new Group();
        final Rectangle selector = getSelector()
                .setWidth( width )
                .setHeight( height )
                .setListening( false );
        g.add( selector );
        return g;
    }

    /**
     * Delegate construction of the "selector" to sub-classes. All implementations
     * are to provide a Rectangle surrounding the whole GridWidget.
     * @return
     */
    public abstract Rectangle getSelector();

    @Override
    public Group renderHeader( final GridData model,
                               final GridHeaderRenderContext context ) {
        final int startColumnIndex = context.getStartColumnIndex();
        final int endColumnIndex = context.getEndColumnIndex();
        final double width = context.getWidth();

        final Group g = new Group();
        final Rectangle header = getHeaderBackground()
                .setWidth( width )
                .setHeight( getHeaderHeight() )
                .setListening( false );
        g.add( header );

        final List<GridColumn<?>> columns = model.getColumns();

        //Linked columns
        double x = 0;
        for ( int i = startColumnIndex; i <= endColumnIndex; i++ ) {
            final GridColumn column = columns.get( i );
            if ( column.isVisible() ) {
                final double w = column.getWidth();
                if ( column.isLinked() ) {
                    final Rectangle lr = getHeaderLinkBackground()
                            .setWidth( w )
                            .setHeight( getHeaderHeight() )
                            .setX( x );
                    g.add( lr );
                }
                x = x + w;
            }
        }

        //Grid lines
        x = 0;
        final MultiPath headerGrid = getHeaderGridLine().setListening( false );
        for ( int i = startColumnIndex; i <= endColumnIndex; i++ ) {
            final GridColumn column = columns.get( i );
            if ( column.isVisible() ) {
                headerGrid.M( x, 0 ).L( x,
                                        getHeaderHeight() );
                x = x + column.getWidth();
            }
        }
        g.add( headerGrid );

        //Column title
        x = 0;
        for ( int i = startColumnIndex; i <= endColumnIndex; i++ ) {
            final GridColumn column = columns.get( i );
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

    /**
     * Delegate the Header's background Rectangle to sub-classes.
     * @return
     */
    public abstract Rectangle getHeaderBackground();

    /**
     * Delegate the Header's grid lines to sub-classes.
     * @return
     */
    public abstract MultiPath getHeaderGridLine();

    /**
     * Delegate the Header's background Rectangle, used for "linked" columns to sub-classes.
     * @return
     */
    public abstract Rectangle getHeaderLinkBackground();

    @Override
    public Group renderBody( final GridData model,
                             final GridBodyRenderContext context ) {
        final int startColumnIndex = context.getStartColumnIndex();
        final int endColumnIndex = context.getEndColumnIndex();
        final int startRowIndex = context.getStartRowIndex();
        final int endRowIndex = context.getEndRowIndex();
        final double width = context.getWidth();
        final Transform transform = context.getTransform();
        final BaseGridWidget<?, ?> widget = context.getWidget();

        final Group g = new Group();
        final List<GridColumn<?>> columns = model.getColumns();

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
        final MultiPath bodyGrid = getBodyGridLine().setListening( false );
        double x = 0;
        for ( int i = startColumnIndex; i <= endColumnIndex; i++ ) {
            final GridColumn column = columns.get( i );
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
            final GridRow row = model.getRow( rowIndex );
            x = 0;
            for ( int columnIndex = startColumnIndex; columnIndex <= endColumnIndex; columnIndex++ ) {
                final GridColumn column = columns.get( columnIndex );
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

    /**
     * Delegate the Body's background Rectangle to sub-classes.
     * @return
     */
    public abstract Rectangle getBodyBackground();

    /**
     * Delegate the Body's grid lines to sub-classes.
     * @return
     */
    public abstract MultiPath getBodyGridLine();

}
