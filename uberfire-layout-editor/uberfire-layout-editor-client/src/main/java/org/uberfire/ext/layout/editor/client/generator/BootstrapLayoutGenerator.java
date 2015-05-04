package org.uberfire.ext.layout.editor.client.generator;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidContainer;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.base.DivWidget;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.components.RenderingContext;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;

/**
 * A bootstrap based layout generator
 */
@ApplicationScoped
public class BootstrapLayoutGenerator implements LayoutGenerator {

    @Inject SyncBeanManager beanManager;

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

    private void generateComponents( LayoutColumn layoutColumn, Column column ) {
        for ( LayoutComponent layoutComponent : layoutColumn.getLayoutComponents() ) {

            for (IOCBeanDef beanDef : beanManager.lookupBeans(layoutComponent.getDragTypeName())) {
                LayoutDragComponent dragComponent = (LayoutDragComponent) beanDef.getInstance();
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
