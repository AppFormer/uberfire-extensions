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
package org.uberfire.ext.wires.core.grids.client.widget.dom;

import java.util.Iterator;

import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.event.NodeMouseUpEvent;
import com.ait.lienzo.client.core.types.Transform;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.wires.core.grids.client.model.IGridCell;
import org.uberfire.ext.wires.core.grids.client.widget.BaseGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridCellRenderContext;

/**
 * The base of all DOMElements, providing common services such as Browser event propagation. MouseEvents do not bubble
 * from an absolutely positioned DIV to other DOM elements; such as the Canvas. This class therefore emulates event
 * propagation by passing MouseEvents to canvas Layer and Grid Widget.
 * @param <T> The data-type represented by the DOMElement.
 * @param <W> The Widget to be wrapped by the DOMElement.
 */
public abstract class BaseDOMElement<T, W extends Widget> {

    private static final double EPSILON = 0.0000001;
    private static final NumberFormat FORMAT = NumberFormat.getFormat( "0.0000" );

    private static final Command NOP_COMMAND = new Command() {
        @Override
        public void execute() {
            //Do nothing
        }
    };

    protected final W widget;
    protected final SimplePanel widgetContainer = new SimplePanel();

    protected final GridLayer gridLayer;
    protected final BaseGridWidget<?, ?> gridWidget;
    protected final AbsolutePanel domElementContainer;

    public BaseDOMElement( final W widget,
                           final GridLayer gridLayer,
                           final BaseGridWidget<?, ?> gridWidget,
                           final AbsolutePanel domElementContainer ) {
        this.widget = widget;
        this.gridLayer = gridLayer;
        this.gridWidget = gridWidget;
        this.domElementContainer = domElementContainer;

        final Style style = widgetContainer.getElement().getStyle();
        style.setPosition( Style.Position.ABSOLUTE );

        //MouseEvents over absolutely positioned elements do not bubble through the DOM.
        //Consequentially Event Handlers on GridLayer do not receive notification of MouseMove
        //Events used during column resizing. Therefore we manually bubble events to GridLayer.
        widgetContainer.addDomHandler( new MouseDownHandler() {
            @Override
            public void onMouseDown( final MouseDownEvent event ) {
                gridLayer.onNodeMouseDown( new NodeMouseDownEvent( event ) {

                    @Override
                    public int getX() {
                        //Adjust the x-coordinate (relative to the DOM Element) to be relative to the GridCanvas.
                        return super.getX() + widgetContainer.getElement().getOffsetLeft();
                    }

                    @Override
                    public int getY() {
                        //Adjust the y-coordinate (relative to the DOM Element) to be relative to the GridCanvas.
                        return super.getY() + widgetContainer.getElement().getOffsetTop();
                    }

                } );
            }
        }, MouseDownEvent.getType() );
        widgetContainer.addDomHandler( new MouseMoveHandler() {
            @Override
            public void onMouseMove( final MouseMoveEvent event ) {
                //The DOM Element changes the Cursor, so set to the state determined by the MouseEvent Handlers on GridLayer
                style.setCursor( gridLayer.getGridWidgetHandlersState().getCursor() );

                gridLayer.onNodeMouseMove( new NodeMouseMoveEvent( event ) {

                    @Override
                    public int getX() {
                        //Adjust the x-coordinate (relative to the DOM Element) to be relative to the GridCanvas.
                        return super.getX() + widgetContainer.getElement().getOffsetLeft();
                    }

                    @Override
                    public int getY() {
                        //Adjust the y-coordinate (relative to the DOM Element) to be relative to the GridCanvas.
                        return super.getY() + widgetContainer.getElement().getOffsetTop();
                    }

                } );
            }
        }, MouseMoveEvent.getType() );
        widgetContainer.addDomHandler( new MouseUpHandler() {
            @Override
            public void onMouseUp( final MouseUpEvent event ) {
                gridLayer.onNodeMouseUp( new NodeMouseUpEvent( event ) {

                    @Override
                    public int getX() {
                        //Adjust the x-coordinate (relative to the DOM Element) to be relative to the GridCanvas.
                        return super.getX() + widgetContainer.getElement().getOffsetLeft();
                    }

                    @Override
                    public int getY() {
                        //Adjust the y-coordinate (relative to the DOM Element) to be relative to the GridCanvas.
                        return super.getY() + widgetContainer.getElement().getOffsetTop();
                    }

                } );
            }
        }, MouseUpEvent.getType() );
    }

    /**
     * Initialise the DOMElement for the given cell and render context.
     * @param cell The cell requiring a DOMElement.
     * @param context The render context for the cell.
     */
    protected abstract void initialise( final IGridCell<T> cell,
                                        final GridCellRenderContext context );

    /**
     * Flush the state of the GWT Widget to the underlying GridWidget and execute the given command on completion.
     * @param command
     */
    public abstract void flush( final Command command );

    /**
     * Write the cell to a Widget
     * @param cell The cell to be represented by the Widget
     * @param context The cell's GridCellRenderContext context
     */
    public abstract void toWidget( final IGridCell<T> cell,
                                   final GridCellRenderContext context );

    /**
     * Read the value of a cell from a Widget
     * @return
     */
    public abstract T fromWidget();

    /**
     * Get a GWT Widget for the DOMElement.
     * @return
     */
    public W getWidget() {
        return widget;
    }

    /**
     * Flush the state of the GWT Widget to the underlying GridWidget.
     */
    public void flush() {
        flush( NOP_COMMAND );
    }

    /**
     * Get the container for the GWT Widget.
     * @return
     */
    protected SimplePanel getContainer() {
        return widgetContainer;
    }

    /**
     * Transform the DOMElement based on the render context, such as scale and position.
     * @param context
     */
    protected void transform( final GridCellRenderContext context ) {
        final Transform transform = context.getTransform();
        final double width = context.getWidth();
        final double height = context.getHeight();

        final Style style = widgetContainer.getElement().getStyle();

        //Reposition and transform the DOM Element
        style.setLeft( ( context.getX() * transform.getScaleX() ) + transform.getTranslateX(),
                       Style.Unit.PX );
        style.setTop( ( context.getY() * transform.getScaleY() ) + transform.getTranslateY(),
                      Style.Unit.PX );
        style.setWidth( width,
                        Style.Unit.PX );
        style.setHeight( height,
                         Style.Unit.PX );

        // --- Workaround for BS2 ---
        style.setProperty( "WebkitBoxSizing",
                           "border-box" );
        style.setProperty( "MozBoxSizing",
                           "border-box" );
        style.setProperty( "boxSizing",
                           "border-box" );
        style.setProperty( "lineHeight",
                           "normal" );
        // --- End workaround ---

        if ( isOne( transform.getScaleX() ) && isOne( transform.getScaleY() ) ) {
            style.clearProperty( "WebkitTransform" );
            style.clearProperty( "MozTransform" );
            style.clearProperty( "Transform" );
            return;
        }

        final String scale = "scale(" + FORMAT.format( transform.getScaleX() ) + ", " + FORMAT.format( transform.getScaleY() ) + ")";
        final String translate = "translate(" + FORMAT.format( ( ( width - width * transform.getScaleX() ) / -2.0 ) ) + "px, " + FORMAT.format( ( ( height - height * transform.getScaleY() ) / -2.0 ) ) + "px)";
        style.setProperty( "WebkitTransform",
                           translate + " " + scale );
        style.setProperty( "MozTransform",
                           translate + " " + scale );
        style.setProperty( "Transform",
                           translate + " " + scale );
    }

    //Convenience method to check if a double is "almost" one.
    private boolean isOne( final double value ) {
        return value >= 1.0 - EPSILON && value <= 1.0 + EPSILON;
    }

    /**
     * Attach the DOMElement to the GWT container, if not already attached.
     */
    public void attach() {
        final Iterator<Widget> itr = domElementContainer.iterator();
        while ( itr.hasNext() ) {
            if ( itr.next().equals( widgetContainer ) ) {
                return;
            }
        }
        //When an Element is detached it's Position configuration is cleared, so reset it
        final Style style = widgetContainer.getElement().getStyle();
        style.setPosition( Style.Position.ABSOLUTE );

        domElementContainer.add( widgetContainer );
    }

    /**
     * Detach the DOMElement from the GWT container, if already attached.
     */
    public void detach() {
        final Iterator<Widget> itr = domElementContainer.iterator();
        while ( itr.hasNext() ) {
            if ( itr.next().equals( widgetContainer ) ) {
                itr.remove();
                return;
            }
        }
    }

}
