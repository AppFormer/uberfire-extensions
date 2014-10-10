/*
 * Copyright 2014 JBoss Inc
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
package org.kie.uberfire.wires.core.client.canvas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;

import com.emitrom.lienzo.client.core.shape.GridLayer;
import com.emitrom.lienzo.client.core.shape.IPrimitive;
import com.emitrom.lienzo.client.core.shape.Layer;
import com.emitrom.lienzo.client.core.shape.Line;
import com.emitrom.lienzo.shared.core.types.ColorName;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import org.kie.uberfire.wires.core.api.containers.WiresContainer;
import org.kie.uberfire.wires.core.api.controlpoints.HasControlPoints;
import org.kie.uberfire.wires.core.api.magnets.HasMagnets;
import org.kie.uberfire.wires.core.api.magnets.Magnet;
import org.kie.uberfire.wires.core.api.magnets.MagnetManager;
import org.kie.uberfire.wires.core.api.magnets.RequiresMagnetManager;
import org.kie.uberfire.wires.core.api.selection.SelectionManager;
import org.kie.uberfire.wires.core.api.shapes.RequiresShapesManager;
import org.kie.uberfire.wires.core.api.shapes.ShapesManager;
import org.kie.uberfire.wires.core.api.shapes.WiresBaseShape;
import org.kie.uberfire.wires.core.api.shapes.WiresShape;
import org.kie.uberfire.wires.core.client.progressbar.ProgressBar;

/**
 * This is the root Canvas provided by Wires
 */
public class WiresCanvas extends Composite implements ShapesManager,
                                                      SelectionManager,
                                                      MagnetManager {

    private FocusableLienzoPanel panel;
    private WiresBaseShape selectedShape;
    private ProgressBar progressBar;

    protected Layer canvasLayer;
    protected List<WiresBaseShape> shapesInCanvas = new ArrayList<WiresBaseShape>();

    @PostConstruct
    public void init() {
        panel = new FocusableLienzoPanel( 1000,
                                          1000 );

        initWidget( panel );

        //This is an optimization to ensure only one draw() is called per browser loop. The Wires
        //framework makes calls to draw() from different places for different purposes. To avoid
        //multiple calls inside a single browser loop we simply record a draw() request has been
        //received and schedule it's invocation for later.
        canvasLayer = new Layer() {

            private boolean scheduled = false;

            @Override
            public void draw() {
                if ( !scheduled ) {
                    scheduled = true;
                    Scheduler.get().scheduleFinally( new Command() {
                        @Override
                        public void execute() {
                            doDraw();
                        }
                    } );
                }
            }

            private void doDraw() {
                scheduled = false;
                super.draw();
            }
        };

        //Grid...
        Line line1 = new Line( 0,
                               0,
                               0,
                               0 ).setStrokeColor( ColorName.BLUE ).setAlpha( 0.5 ); // primary lines
        Line line2 = new Line( 0,
                               0,
                               0,
                               0 ).setStrokeColor( ColorName.GREEN ).setAlpha( 0.5 ); // secondary dashed-lines
        line2.setDashArray( 2,
                            2 );

        GridLayer gridLayer = new GridLayer( 100,
                                             line1,
                                             25,
                                             line2 );
        panel.setBackgroundLayer( gridLayer );

        panel.getScene().add( canvasLayer );
    }

    public boolean hasProgressBar() {
        return this.progressBar != null;
    }

    public ProgressBar getProgressBar() {
        return this.progressBar;
    }

    public void setProgressBar( final ProgressBar progressBar ) {
        this.progressBar = progressBar;
        canvasLayer.add( progressBar );
        canvasLayer.draw();
    }

    @Override
    public List<WiresBaseShape> getShapesInCanvas() {
        return Collections.unmodifiableList( this.shapesInCanvas );
    }

    @Override
    public void addShape( final WiresBaseShape shape ) {
        //Attach relevant handlers
        shape.setSelectionManager( this );
        if ( shape instanceof RequiresShapesManager ) {
            ( (RequiresShapesManager) shape ).setShapesManager( this );
        }
        if ( shape instanceof RequiresMagnetManager ) {
            ( (RequiresMagnetManager) shape ).setMagnetManager( this );
        }

        canvasLayer.add( shape );
        shapesInCanvas.add( shape );

        //Containers are always at the bottom of the render stack
        if ( shape instanceof WiresContainer ) {
            shape.moveToBottom();
        }

        canvasLayer.draw();
    }

    @Override
    public void deleteShape( final WiresBaseShape shape ) {
        shape.destroy();
        deselectShape( shape );
        canvasLayer.remove( shape );
        shapesInCanvas.remove( shape );
        canvasLayer.draw();
    }

    @Override
    public void forceDeleteShape( final WiresBaseShape shape ) {
        deleteShape( shape );
    }

    public void clear() {
        //Detach Shapes in Containers; as destroying a Container automatically destroys it's contained Shapes
        //This sounds as though we need not worry about those, however "shapesInCanvas" this means we cannot
        //simply iterate over "shapesInCanvas" as it's content changes as Shapes are destroyed.
        for ( WiresShape shape : shapesInCanvas ) {
            if ( shape instanceof WiresContainer ) {
                final WiresContainer wc = (WiresContainer) shape;
                for ( WiresBaseShape bc : wc.getContainedShapes() ) {
                    wc.detachShape( bc );
                }
            }
        }

        //Now it's safe to destroy all Shapes
        for ( WiresShape shape : shapesInCanvas ) {
            shape.destroy();
            canvasLayer.remove( (IPrimitive<?>) shape );
        }
        clearSelection();
        shapesInCanvas.clear();
        canvasLayer.draw();
    }

    @Override
    public void clearSelection() {
        selectedShape = null;
        for ( WiresShape shape : getShapesInCanvas() ) {
            shape.setSelected( false );
            if ( shape instanceof HasControlPoints ) {
                ( (HasControlPoints) shape ).hideControlPoints();
            }
            if ( shape instanceof HasMagnets ) {
                ( (HasMagnets) shape ).hideMagnetPoints();
            }
        }
        canvasLayer.draw();
    }

    @Override
    public void selectShape( final WiresBaseShape shape ) {
        if ( shape == null ) {
            return;
        }
        if ( shape.equals( selectedShape ) ) {
            return;
        }
        clearSelection();
        selectedShape = shape;
        selectedShape.setSelected( true );
        if ( shape instanceof HasControlPoints ) {
            ( (HasControlPoints) selectedShape ).showControlPoints();
        }
        canvasLayer.draw();
    }

    @Override
    public void deselectShape( final WiresBaseShape shape ) {
        if ( shape == null ) {
            return;
        }
        selectedShape = null;
        if ( shape instanceof HasControlPoints ) {
            ( (HasControlPoints) shape ).hideControlPoints();
        }
        if ( shape instanceof HasMagnets ) {
            ( (HasMagnets) shape ).hideMagnetPoints();
        }
        canvasLayer.draw();
    }

    @Override
    public boolean isShapeSelected() {
        return selectedShape != null;
    }

    @Override
    public WiresBaseShape getSelectedShape() {
        return selectedShape;
    }

    @Override
    public void hideAllMagnets() {
        for ( WiresShape shape : getShapesInCanvas() ) {
            if ( shape instanceof HasMagnets ) {
                final HasMagnets mShape = (HasMagnets) shape;
                mShape.hideMagnetPoints();
            }
        }
    }

    @Override
    public Magnet getMagnet( final WiresShape activeShape,
                             final double cx,
                             final double cy ) {
        if ( activeShape == null ) {
            return null;
        }

        Magnet selectedMagnet = null;
        double finalDistance = Double.MAX_VALUE;
        for ( WiresShape shape : getShapesInCanvas() ) {
            if ( !shape.getId().equals( activeShape.getId() ) ) {
                if ( shape instanceof HasMagnets ) {
                    final HasMagnets mShape = (HasMagnets) shape;
                    if ( shape.contains( cx,
                                         cy ) ) {
                        mShape.showMagnetsPoints();
                        final List<Magnet> magnets = mShape.getMagnets();
                        for ( Magnet magnet : magnets ) {
                            magnet.setActive( false );

                            double deltaX = cx - magnet.getX() - magnet.getOffset().getX();
                            double deltaY = cy - magnet.getY() - magnet.getOffset().getY();
                            double distance = Math.sqrt( Math.pow( deltaX, 2 ) + Math.pow( deltaY, 2 ) );

                            if ( finalDistance > distance ) {
                                finalDistance = distance;
                                if ( selectedMagnet != null ) {
                                    selectedMagnet.setActive( false );
                                }
                                selectedMagnet = magnet;
                            }
                        }
                        if ( selectedMagnet != null ) {
                            selectedMagnet.setActive( true );
                        }

                    } else {
                        mShape.hideMagnetPoints();
                    }
                }
            }
        }

        return selectedMagnet;
    }

}
