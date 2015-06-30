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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.wires.core.grids.client.model.IGridCell;
import org.uberfire.ext.wires.core.grids.client.widget.BaseGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridCellRenderContext;

/**
 * Base Factory for multi-instance DOMElements, i.e. there can be more than one "on screen" at any given time.
 * This implementation keeps track of a List of DOMElements used during a render phase. DOMElements are re-used
 * for subsequent render phases, freeing unused DOMElements at the end of the render phase. When a column
 * is not rendered all DOMElements are destroyed.
 * @param <T> The data-type of the cell
 * @param <W> The Widget to be wrapped by the DOMElement.
 * @param <E> The DOMElement type that this Factory generates.
 */
public abstract class BaseDOMElementFactory<T, W extends Widget, E extends BaseDOMElement> implements IDOMElementFactory<T, W, E> {

    protected final GridLayer gridLayer;
    protected final BaseGridWidget<?, ?> gridWidget;
    protected final AbsolutePanel domElementContainer;

    protected final List<E> domElements = new ArrayList<E>();

    private int consumed = 0;

    public BaseDOMElementFactory( final GridLayer gridLayer,
                                  final BaseGridWidget<?, ?> gridWidget,
                                  final AbsolutePanel domElementContainer ) {
        this.gridLayer = gridLayer;
        this.gridWidget = gridWidget;
        this.domElementContainer = domElementContainer;
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
        E domElement;
        if ( consumed + 1 > domElements.size() ) {
            domElement = createDomElement( gridLayer,
                                           gridWidget,
                                           domElementContainer );
            domElements.add( domElement );
        } else {
            domElement = domElements.get( consumed );
        }
        consumed++;
        domElement.initialise( cell,
                               context );
        domElement.attach();
        callback.callback( domElement );
    }

    @Override
    public void initialiseResources() {
        consumed = 0;
    }

    @Override
    public void destroyResources() {
        for ( E domElement : domElements ) {
            domElement.detach();
        }
        domElements.clear();
        consumed = 0;
    }

    @Override
    public void freeUnusedResources() {
        final List<E> freedDomElements = new ArrayList<E>();
        for ( int i = consumed; i < domElements.size(); i++ ) {
            final E domElement = domElements.get( i );
            domElement.detach();
            freedDomElements.add( domElement );
        }
        for ( E domElement : freedDomElements ) {
            domElements.remove( domElement );
        }
    }

}
