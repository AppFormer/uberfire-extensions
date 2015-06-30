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

import com.google.gwt.user.client.ui.AbsolutePanel;
import org.uberfire.client.callbacks.Callback;
import org.uberfire.ext.wires.core.grids.client.model.IGridCell;
import org.uberfire.ext.wires.core.grids.client.model.IHasResources;
import org.uberfire.ext.wires.core.grids.client.widget.BaseGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridCellRenderContext;

/**
 * Definition of a Factor that can create DOMElements for GWT Widget based cell content.
 * DOMElements are transient in nature and only exist when required, such as when a column
 * and row is visible or when a cell is being edited.
 * @param <T> The data-type of the cell
 * @param <W> The Widget to be wrapped by the DOMElement.
 * @param <E> The DOMElement type that this Factory generates.
 */
public interface IDOMElementFactory<T, W, E> extends IHasResources {

    /**
     * Create a Widget to be wrapped by the DOMElement
     * @return
     */
    W createWidget();

    /**
     * Create a DOMElement.
     * @param gridLayer The Lienzo layer on which the Grid Widget is attached. DOMElements may need to redraw the Layer when their state changes.
     * @param gridWidget The GridWidget to which this DOMElement is to be associated.
     * @param domElementContainer The GWT container for the DOMElement.
     * @return
     */
    E createDomElement( final GridLayer gridLayer,
                        final BaseGridWidget<?, ?> gridWidget,
                        final AbsolutePanel domElementContainer );

    /**
     * Initialise a DOMElement for a cell and attach it to the GWT container.
     * @param cell The cell requiring the DOMElement.
     * @param context The render context of the cell.
     */
    void attachDomElement( final IGridCell<T> cell,
                           final GridCellRenderContext context );

    /**
     * Initialise a DOMElement for a cell and attach it to the GWT container. The callback
     * can be used to perform post-attachment activities, such as setting the focus.
     * @param cell The cell requiring the DOMElement.
     * @param context The render context of the cell.
     * @param callback A callback that is invoked after the cell has been initialised.
     */
    void attachDomElement( final IGridCell<T> cell,
                           final GridCellRenderContext context,
                           final Callback<E> callback );

}
