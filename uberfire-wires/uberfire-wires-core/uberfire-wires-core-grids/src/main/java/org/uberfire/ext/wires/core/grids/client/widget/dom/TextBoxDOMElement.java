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

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.AbsolutePanel;
import org.uberfire.ext.wires.core.grids.client.model.BaseGridCellValue;
import org.uberfire.ext.wires.core.grids.client.model.IGridCell;
import org.uberfire.ext.wires.core.grids.client.model.IHasResources;
import org.uberfire.ext.wires.core.grids.client.widget.BaseGridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridCellRenderContext;

/**
 * A DOMElement for TextBoxes.
 */
public class TextBoxDOMElement extends BaseDOMElement<String, TextBox> {

    private static final int HEIGHT = 16;

    private GridCellRenderContext context;

    public TextBoxDOMElement( final TextBox widget,
                              final GridLayer gridLayer,
                              final BaseGridWidget<?, ?> gridWidget,
                              final IHasResources domElementFactory,
                              final AbsolutePanel domElementContainer ) {
        super( widget,
               gridLayer,
               gridWidget,
               domElementContainer );
        final Style style = widget.getElement().getStyle();
        style.setWidth( 100,
                        Style.Unit.PCT );
        style.setHeight( HEIGHT,
                         Style.Unit.PX );
        style.setPaddingLeft( 2,
                              Style.Unit.PX );
        style.setPaddingRight( 2,
                               Style.Unit.PX );
        style.setFontSize( 10,
                           Style.Unit.PX );

        // --- Workaround for BS2 ---
        style.setPosition( Style.Position.RELATIVE );
        style.setPaddingTop( 0,
                             Style.Unit.PX );
        style.setPaddingBottom( 0,
                                Style.Unit.PX );
        style.setProperty( "WebkitBoxSizing",
                           "border-box" );
        style.setProperty( "MozBoxSizing",
                           "border-box" );
        style.setProperty( "boxSizing",
                           "border-box" );
        style.setProperty( "lineHeight",
                           "normal" );
        // --- End workaround ---

        getContainer().getElement().getStyle().setPaddingLeft( 5,
                                                               Style.Unit.PX );
        getContainer().getElement().getStyle().setPaddingRight( 5,
                                                                Style.Unit.PX );
        getContainer().setWidget( widget );

        widget.addValueChangeHandler( new ValueChangeHandler<String>() {
            @Override
            public void onValueChange( final ValueChangeEvent event ) {
                flush();
            }
        } );
        widget.addBlurHandler( new BlurHandler() {
            @Override
            public void onBlur( final BlurEvent event ) {
                if ( context != null ) {
                    domElementFactory.freeUnusedResources();
                }
            }
        } );
    }

    @Override
    public void initialise( final IGridCell<String> cell,
                            final GridCellRenderContext context ) {
        this.context = context;
        final Style style = widget.getElement().getStyle();
        style.setMarginTop( ( context.getHeight() - HEIGHT ) / 2,
                            Style.Unit.PX );
        transform( context );

        toWidget( cell,
                  context );
    }

    @Override
    public void flush( final Command command ) {
        if ( context != null ) {
            final int rowIndex = context.getRowIndex();
            final int columnIndex = context.getColumnIndex();
            context = null;

            final String value = fromWidget();
            if ( value.trim().isEmpty() ) {
                gridWidget.getModel().deleteCell( rowIndex,
                                                  columnIndex );

            } else {
                gridWidget.getModel().setCell( rowIndex,
                                               columnIndex,
                                               new BaseGridCellValue<String>( value ) );
            }
            gridLayer.draw( command );

        } else {
            command.execute();
        }
    }

    @Override
    @SuppressWarnings("unused")
    public void toWidget( final IGridCell<String> cell,
                          final GridCellRenderContext context ) {
        final String value = cell.getValue().getValue();
        widget.setValue( value );
    }

    @Override
    public String fromWidget() {
        return widget.getValue();
    }

}
