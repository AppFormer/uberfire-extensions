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
package org.uberfire.ext.wires.core.grids.client.util;

import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import org.uberfire.ext.wires.core.grids.client.widget.BaseGridWidget;

/**
 * Utilities class
 */
public class GridCoordinateUtils {

    /**
     * Convert a Canvas/DOM-relative coordinate to one within a GridWidget, taking
     * the current transformation (translation and scale) into consideration.
     * @param gridWidget The GridWidget to which we need to find the relative coordinate
     * @param point The Canvas/DOM MouseEvent coordinate
     * @return A coordinate relative to the GridWidget (in un-transformed coordinate space)
     */
    public static Point2D mapToGridWidgetAbsolutePoint( final BaseGridWidget gridWidget,
                                                        final Point2D point ) {
        final Viewport viewport = gridWidget.getViewport();
        Transform transform = viewport.getTransform();
        if ( transform == null ) {
            viewport.setTransform( transform = new Transform() );
        }

        transform = transform.copy().getInverse();
        final Point2D p = new Point2D( point.getX(),
                                       point.getY() );
        transform.transform( p,
                             p );
        return p.add( gridWidget.getLocation().mul( -1.0 ) );
    }

}
