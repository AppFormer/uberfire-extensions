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
package org.uberfire.ext.wires.core.grids.client.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseDownHandler;
import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.event.NodeMouseMoveHandler;
import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.event.NodeMouseUpHandler;
import com.ait.lienzo.client.core.shape.Arrow;
import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.shared.core.types.ArrowType;
import com.ait.lienzo.shared.core.types.ColorName;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import org.uberfire.ext.wires.core.grids.client.model.IGridColumn;
import org.uberfire.ext.wires.core.grids.client.model.IGridData;
import org.uberfire.ext.wires.core.grids.client.widget.animation.GridWidgetScrollIntoViewAnimation;
import org.uberfire.ext.wires.core.grids.client.widget.basic.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetHandlersState;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetMouseDownHandler;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetMouseMoveHandler;
import org.uberfire.ext.wires.core.grids.client.widget.dnd.GridWidgetMouseUpHandler;

/**
 * A specialised Layer that supports pass-through of MouseEvents from DOMElements to GridWidgets.
 * It also guarantees that Layer.draw() will only be invoked once per browser-loop; by scheduling
 * the actual draw() to GWT's Schedule scheduleFinally(). Furthermore this implementation handles
 * drawing connectors between "linked" grids and acts as a ISelection manager.
 */
public class GridLayer extends Layer implements ISelectionManager,
                                                NodeMouseDownHandler,
                                                NodeMouseMoveHandler,
                                                NodeMouseUpHandler {

    private Map<IGridData<?, ?, ?>, BaseGridWidget<?, ?>> selectables = new HashMap<IGridData<?, ?, ?>, BaseGridWidget<?, ?>>();
    private Map<GridWidgetConnector, Arrow> connectors = new HashMap<GridWidgetConnector, Arrow>();

    private Rectangle bounds;
    private boolean isRedrawScheduled = false;

    private final GridWidgetMouseDownHandler mouseDownHandler;
    private final GridWidgetMouseMoveHandler mouseMoveHandler;
    private final GridWidgetMouseUpHandler mouseUpHandler;
    private final GridWidgetHandlersState state = new GridWidgetHandlersState();

    private static final Command NOP_COMMAND = new Command() {
        @Override
        public void execute() {
            //Do nothing
        }
    };

    public GridLayer() {
        bounds = new Rectangle( 0, 0 )
                .setVisible( false );
        add( bounds );

        //Column DnD handlers
        mouseDownHandler = new GridWidgetMouseDownHandler( this,
                                                           state,
                                                           selectables );
        mouseMoveHandler = new GridWidgetMouseMoveHandler( this,
                                                           state,
                                                           selectables );
        mouseUpHandler = new GridWidgetMouseUpHandler( this,
                                                       state,
                                                       selectables );
        addNodeMouseDownHandler( mouseDownHandler );
        addNodeMouseMoveHandler( mouseMoveHandler );
        addNodeMouseUpHandler( mouseUpHandler );
    }

    @Override
    public void onNodeMouseDown( final NodeMouseDownEvent event ) {
        mouseDownHandler.onNodeMouseDown( event );
    }

    @Override
    public void onNodeMouseMove( final NodeMouseMoveEvent event ) {
        mouseMoveHandler.onNodeMouseMove( event );
    }

    @Override
    public void onNodeMouseUp( final NodeMouseUpEvent event ) {
        mouseUpHandler.onNodeMouseUp( event );
    }

    public GridWidgetHandlersState getGridWidgetHandlersState() {
        return this.state;
    }

    /**
     * Schedule a draw with out additional command.
     */
    @Override
    public void draw() {
        draw( NOP_COMMAND );
    }

    /**
     * Schedule a draw with a command to be executed once the draw() has completed.
     * @param command
     */
    public void draw( final Command command ) {
        if ( !isRedrawScheduled ) {
            isRedrawScheduled = true;
            Scheduler.get().scheduleFinally( new Command() {

                //This is helpful when debugging rendering issues to set the bounds smaller than the Viewport
                private static final int PADDING = 0;

                @Override
                public void execute() {
                    updateBounds();
                    updateConnectors();
                    GridLayer.super.draw();
                    isRedrawScheduled = false;
                    command.execute();
                }

                private void updateBounds() {
                    final Viewport viewport = GridLayer.this.getViewport();
                    Transform transform = viewport.getTransform();
                    if ( transform == null ) {
                        viewport.setTransform( transform = new Transform() );
                    }
                    final double x = ( PADDING - transform.getTranslateX() ) / transform.getScaleX();
                    final double y = ( PADDING - transform.getTranslateY() ) / transform.getScaleY();
                    bounds.setLocation( new Point2D( x,
                                                     y ) );
                    bounds.setHeight( ( viewport.getHeight() - PADDING * 2 ) / transform.getScaleX() );
                    bounds.setWidth( ( viewport.getWidth() - PADDING * 2 ) / transform.getScaleY() );
                    bounds.setStrokeWidth( 1.0 / transform.getScaleX() );
                }

                private void updateConnectors() {
                    for ( Map.Entry<GridWidgetConnector, Arrow> e : connectors.entrySet() ) {
                        final GridWidgetConnector connector = e.getKey();
                        final Arrow arrow = e.getValue();
                        final IGridColumn<?, ?> sourceColumn = connector.getSourceColumn();
                        final IGridColumn<?, ?> targetColumn = connector.getTargetColumn();
                        final BaseGridWidget<?, ?> sourceGrid = getLinkedGrid( sourceColumn );
                        final BaseGridWidget<?, ?> targetGrid = getLinkedGrid( targetColumn );
                        if ( connector.getDirection() == GridWidgetConnector.Direction.EAST_WEST ) {
                            arrow.setStart( new Point2D( sourceGrid.getX() + sourceGrid.getWidth() / 2,
                                                         arrow.getStart().getY() ) );
                        } else {
                            arrow.setEnd( new Point2D( targetGrid.getX() + targetGrid.getWidth(),
                                                       arrow.getEnd().getY() ) );
                        }
                    }

                }
            } );
        }
    }

    /**
     * Add a child to this Layer. If the child is a GridWidget then also add
     * a Connector between the Grid Widget and any "linked" GridWidgets.
     * @param child
     * @return
     */
    @Override
    public Layer add( final IPrimitive<?> child ) {
        addSelectable( child );
        return super.add( child );
    }

    private void addSelectable( final IPrimitive<?> child,
                                final IPrimitive<?>... children ) {
        final List<IPrimitive<?>> all = new ArrayList<IPrimitive<?>>();
        all.add( child );
        all.addAll( Arrays.asList( children ) );
        for ( IPrimitive<?> c : all ) {
            if ( c instanceof BaseGridWidget<?, ?> ) {
                final BaseGridWidget<?, ?> gridWidget = (BaseGridWidget<?, ?>) c;
                selectables.put( gridWidget.getModel(),
                                 gridWidget );
                addConnectors();
            }
        }
    }

    private void addConnectors() {
        for ( Map.Entry<IGridData<?, ?, ?>, BaseGridWidget<?, ?>> e1 : selectables.entrySet() ) {
            for ( IGridColumn<?, ?> c : e1.getKey().getColumns() ) {
                if ( c.isVisible() ) {
                    if ( c.isLinked() ) {
                        final BaseGridWidget<?, ?> linkWidget = getLinkedGrid( c.getLink() );
                        if ( linkWidget != null ) {
                            GridWidgetConnector.Direction direction;
                            final Point2D sp = new Point2D( e1.getValue().getX() + e1.getValue().getWidth() / 2,
                                                            e1.getValue().getY() + e1.getValue().getHeight() / 2 );
                            final Point2D ep = new Point2D( linkWidget.getX() + linkWidget.getWidth() / 2,
                                                            linkWidget.getY() + linkWidget.getHeight() / 2 );
                            if ( sp.getX() < ep.getX() ) {
                                direction = GridWidgetConnector.Direction.EAST_WEST;
                                sp.setX( sp.getX() + e1.getValue().getWidth() / 2 );
                                ep.setX( ep.getX() - linkWidget.getWidth() / 2 );
                            } else {
                                direction = GridWidgetConnector.Direction.WEST_EAST;
                                sp.setX( sp.getX() - e1.getValue().getWidth() / 2 );
                                ep.setX( ep.getX() + linkWidget.getWidth() / 2 );
                            }

                            final GridWidgetConnector connector = new GridWidgetConnector( c,
                                                                                           c.getLink(),
                                                                                           direction );

                            if ( !connectors.containsKey( connector ) ) {
                                final Arrow arrow = new Arrow( sp,
                                                               ep,
                                                               10.0,
                                                               40.0,
                                                               45.0,
                                                               45.0,
                                                               ArrowType.AT_END )
                                        .setStrokeColor( ColorName.DARKGRAY )
                                        .setFillColor( ColorName.TAN )
                                        .setStrokeWidth( 2.0 );
                                connectors.put( connector,
                                                arrow );
                                super.add( arrow );
                                arrow.moveToBottom();
                            }
                        }
                    }
                }
            }
        }
    }

    private BaseGridWidget<?, ?> getLinkedGrid( final IGridColumn<?, ?> link ) {
        BaseGridWidget<?, ?> gridWidget = null;
        for ( Map.Entry<IGridData<?, ?, ?>, BaseGridWidget<?, ?>> e : selectables.entrySet() ) {
            if ( e.getKey().getColumns().contains( link ) ) {
                gridWidget = e.getValue();
                break;
            }
        }
        return gridWidget;
    }

    /**
     * Add a child and other children to this Layer. If the child or any children is a GridWidget
     * then also add a Connector between the Grid Widget and any "linked" GridWidgets.
     * @param child
     * @return
     */
    @Override
    public Layer add( final IPrimitive<?> child,
                      final IPrimitive<?>... children ) {
        addSelectable( child,
                       children );
        return super.add( child,
                          children );
    }

    /**
     * Remove a child from this Layer. if the child is a GridWidget also remove
     * any Connectors that have been added between the GridWidget being removed
     * and any of GridWidgets.
     * @param child
     * @return
     */
    @Override
    public Layer remove( final IPrimitive<?> child ) {
        removeSelectable( child );
        return super.remove( child );
    }

    private void removeSelectable( final IPrimitive<?> child,
                                   final IPrimitive<?>... children ) {
        final List<IPrimitive<?>> all = new ArrayList<IPrimitive<?>>();
        all.add( child );
        all.addAll( Arrays.asList( children ) );
        for ( IPrimitive<?> c : all ) {
            if ( c instanceof GridWidget ) {
                final GridWidget gridWidget = (GridWidget) c;
                selectables.remove( gridWidget.getModel() );
                removeConnectors( gridWidget.getModel() );
            }
        }
    }

    private void removeConnectors( final IGridData<?, ?, ?> model ) {
        final List<GridWidgetConnector> removedConnectors = new ArrayList<GridWidgetConnector>();
        for ( Map.Entry<GridWidgetConnector, Arrow> e : connectors.entrySet() ) {
            if ( model.getColumns().contains( e.getKey().getSourceColumn() ) || model.getColumns().contains( e.getKey().getTargetColumn() ) ) {
                remove( e.getValue() );
                removedConnectors.add( e.getKey() );
            }
        }
        //Remove Connectors from HashMap after iteration of EntrySet to avoid ConcurrentModificationException
        for ( GridWidgetConnector c : removedConnectors ) {
            connectors.remove( c );
        }
    }

    @Override
    public Layer removeAll() {
        selectables.clear();
        return super.removeAll();
    }

    @Override
    public void select( final IGridData<?, ?, ?> selectable ) {
        for ( Map.Entry<IGridData<?, ?, ?>, BaseGridWidget<?, ?>> e : selectables.entrySet() ) {
            e.getValue().deselect();
        }
        if ( selectables.containsKey( selectable ) ) {
            selectables.get( selectable ).select();
        }
        draw();
    }

    @Override
    public void selectLinkedColumn( final IGridColumn<?, ?> link ) {
        final BaseGridWidget<?, ?> gridWidget = getLinkedGrid( link );
        if ( gridWidget == null ) {
            return;
        }

        final GridWidgetScrollIntoViewAnimation a = new GridWidgetScrollIntoViewAnimation( gridWidget,
                                                                                           new Command() {
                                                                                               @Override
                                                                                               public void execute() {
                                                                                                   select( gridWidget.getModel() );
                                                                                               }
                                                                                           } );
        a.run();
    }

    public Rectangle getVisibleBounds() {
        return bounds;
    }

}
