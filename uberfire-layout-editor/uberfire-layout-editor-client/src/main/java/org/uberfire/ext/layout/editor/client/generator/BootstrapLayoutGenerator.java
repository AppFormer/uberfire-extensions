package org.uberfire.ext.layout.editor.client.generator;

import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidContainer;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.base.DivWidget;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.components.RenderingContext;
import org.uberfire.ext.layout.editor.client.util.DragTypeBeanResolver;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

/**
 * A bootstrap based layout generator
 */
@ApplicationScoped
public class BootstrapLayoutGenerator implements LayoutGenerator {

    @Inject
    DragTypeBeanResolver dragTypeBeanResolver;

    public FluidContainer build(LayoutTemplate layoutTemplate) {
        FluidContainer mainPanel = new FluidContainer();
        mainPanel.getElement().setId( "mainContainer" );
        List<LayoutRow> rows = layoutTemplate.getRows();
        generateRows(rows, mainPanel);
        return mainPanel;
    }

    private void generateRows(List<LayoutRow> rows, DivWidget parentWidget) {
        for ( LayoutRow layoutRow : rows ) {
            FluidRow row = new FluidRow();
            for ( LayoutColumn layoutColumn : layoutRow.getLayoutColumns() ) {
                Column column = new Column( new Integer( layoutColumn.getSpan() ) );
                if ( columnHasNestedRows(layoutColumn) ) {
                    generateRows(layoutColumn.getRows(), column);
                } else {
                    generateComponents(layoutColumn, column );
                }
                row.add( column );
            }
            parentWidget.add( row );
        }
    }

    private void generateComponents(final LayoutColumn layoutColumn, final Column column) {
        for (final LayoutComponent layoutComponent : layoutColumn.getLayoutComponents() ) {

            final LayoutDragComponent dragComponent = dragTypeBeanResolver.lookupDragTypeBean(layoutComponent.getDragTypeName());
            if (dragComponent != null) {
                RenderingContext componentContext = new RenderingContext(layoutComponent, column);
                IsWidget componentWidget = dragComponent.getShowWidget(componentContext);
                if (componentWidget != null) {
                    column.add(componentWidget);
                }
            }
        }
    }

    private boolean columnHasNestedRows( LayoutColumn layoutColumn) {
        return layoutColumn.getRows() != null && !layoutColumn.getRows().isEmpty();
    }
}
