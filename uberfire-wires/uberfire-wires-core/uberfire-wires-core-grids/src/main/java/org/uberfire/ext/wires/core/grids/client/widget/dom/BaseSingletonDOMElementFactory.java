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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.wires.core.grids.client.model.IGridCell;
import org.uberfire.ext.wires.core.grids.client.widget.BaseGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridCellRenderContext;

/**
 * Base Factory for single-instance DOMElements, i.e. there can only be one instance "on screen" at any given time,
 * for example to handle "in cell" editing; when a DOMElement is required to "edit" the cell but not when the cell
 * is rendered ordinarily. This implementation keeps a single DOMElement that is detached from the GWT container
 * when not needed.
 * @param <T> The data-type of the cell
 * @param <W> The Widget to be wrapped by the DOMElement.
 * @param <E> The DOMElement type that this Factory generates.
 */
public abstract class BaseSingletonDOMElementFactory<T, W extends Widget, E extends BaseDOMElement> implements IDOMElementFactory<T, W, E> {

    protected final GridLayer gridLayer;
    protected final BaseGridWidget<?, ?> gridWidget;
    protected final AbsolutePanel domElementContainer;

    protected final E domElement;

    public BaseSingletonDOMElementFactory( final GridLayer gridLayer,
                                           final BaseGridWidget<?, ?> gridWidget,
                                           final AbsolutePanel domElementContainer ) {
        this.gridLayer = gridLayer;
        this.gridWidget = gridWidget;
        this.domElementContainer = domElementContainer;
        this.domElement = createDomElement( gridLayer,
                                            gridWidget,
                                            domElementContainer );
    }

    @Override
    public void attachDomElement( final IGridCell<T> cell,
                                  final GridCellRenderContext context ) {
        attachDomElement( cell,
                          context,
                          new Callback<E>() {
                              @Override
                              public void callback( final E result ) {
                                  //Do nothing
                              }
                          } );
    }

    @Override
    public void attachDomElement( final IGridCell<T> cell,
                                  final GridCellRenderContext context,
                                  final Callback<E> callback ) {
        domElement.flush( new Command() {
            @Override
            public void execute() {
                domElement.initialise( cell,
                                       context );
                domElement.attach();
                callback.callback( domElement );
            }
        } );
    }

    @Override
    public void initialiseResources() {
        destroyResources();
    }

    @Override
    public void destroyResources() {
        domElement.flush();
        domElement.detach();
    }

    @Override
    public void freeUnusedResources() {
        destroyResources();
    }

}
