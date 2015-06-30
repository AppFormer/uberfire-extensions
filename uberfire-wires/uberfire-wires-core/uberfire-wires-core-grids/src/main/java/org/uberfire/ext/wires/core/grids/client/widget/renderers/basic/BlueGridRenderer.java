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

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.shared.core.types.ColorName;

/**
 * A renderer that draws a predominantly blue GridWidget.
 */
public class BlueGridRenderer extends AbstractClippingGridRenderer {

    private static final int HEADER_HEIGHT = 30;

    @Override
    public String getName() {
        return "Blue";
    }

    @Override
    public double getHeaderHeight() {
        return HEADER_HEIGHT;
    }

    @Override
    public Rectangle getSelector() {
        final Rectangle selector = new Rectangle( 0, 0 )
                .setStrokeWidth( 2.0 )
                .setStrokeColor( ColorName.BLUE )
                .setShadow( new Shadow( ColorName.DARKBLUE, 4, 0.0, 0.0 ) );
        return selector;
    }

    @Override
    public Rectangle getHeaderBackground() {
        final Rectangle header = new Rectangle( 0, 0 )
                .setFillColor( ColorName.CYAN )
                .setStrokeColor( ColorName.DARKBLUE )
                .setStrokeWidth( 0.5 );
        return header;
    }

    @Override
    public MultiPath getHeaderGridLine() {
        final MultiPath headerGrid = new MultiPath()
                .setStrokeColor( ColorName.SLATEGRAY )
                .setStrokeWidth( 0.5 )
                .setListening( false );
        return headerGrid;
    }

    @Override
    public Rectangle getHeaderLinkBackground() {
        final Rectangle link = new Rectangle( 0, 0 )
                .setFillColor( ColorName.BROWN )
                .setStrokeColor( ColorName.SLATEGRAY )
                .setStrokeWidth( 0.5 );
        return link;
    }

    @Override
    public Rectangle getBodyBackground() {
        final Rectangle body = new Rectangle( 0, 0 )
                .setFillColor( ColorName.LIGHTCYAN )
                .setStrokeColor( ColorName.SLATEGRAY )
                .setStrokeWidth( 0.5 );
        return body;
    }

    @Override
    public MultiPath getBodyGridLine() {
        final MultiPath bodyGrid = new MultiPath()
                .setStrokeColor( ColorName.SLATEGRAY )
                .setStrokeWidth( 0.5 );
        return bodyGrid;
    }

}
