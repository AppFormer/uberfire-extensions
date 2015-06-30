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
package org.uberfire.ext.wires.core.grids.client.widget.animation;

import java.util.ArrayList;
import java.util.List;

import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.animation.IAnimation;
import com.ait.lienzo.client.core.animation.IAnimationCallback;
import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.client.core.animation.TimedAnimation;
import org.uberfire.ext.wires.core.grids.client.model.mergable.MergableGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.mergable.MergableGridWidget;

/**
 * An animation to expand collapsed rows in a merged block. The cells in
 * the merged block are set to expanded when the animation starts.
 */
public class MergableGridWidgetExpandRowsAnimation extends TimedAnimation {

    public MergableGridWidgetExpandRowsAnimation( final MergableGridWidget gridWidget,
                                                  final int rowIndex,
                                                  final int rowCount,
                                                  final int columnIndex ) {
        super( 500,
               new IAnimationCallback() {

                   private AnimationTweener tweener = AnimationTweener.EASE_OUT;
                   private List<Double> heights = new ArrayList<Double>();

                   @Override
                   public void onStart( final IAnimation iAnimation,
                                        final IAnimationHandle iAnimationHandle ) {
                       //Store the rows' target heights
                       for ( int i = 0; i < rowCount; i++ ) {
                           final MergableGridRow row = gridWidget.getModel().getRow( rowIndex + i );
                           heights.add( row.peekHeight() );
                       }

                       //Mark cells as expanded
                       gridWidget.getModel().expandCell( rowIndex,
                                                         columnIndex );
                   }

                   @Override
                   public void onFrame( final IAnimation iAnimation,
                                        final IAnimationHandle iAnimationHandle ) {
                       //Set the rows' height from zero to their starting height
                       final double pct = assertPct( iAnimation.getPercent() );
                       for ( int i = 1; i < rowCount; i++ ) {
                           final MergableGridRow row = gridWidget.getModel().getRow( rowIndex + i );
                           row.setHeight( pct * heights.get( i ) );
                       }
                       gridWidget.getLayer().draw();
                   }

                   @Override
                   public void onClose( final IAnimation iAnimation,
                                        final IAnimationHandle iAnimationHandle ) {
                       //Do nothing
                   }

                   private double assertPct( final double pct ) {
                       if ( pct < 0 ) {
                           return 0;
                       }
                       if ( pct > 1.0 ) {
                           return 1.0;
                       }
                       return tweener.apply( pct );
                   }
               } );
    }

}
