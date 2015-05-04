package org.uberfire.ext.layout.editor.client.components;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidContainer;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.dnd.DropColumnPanel;
import org.uberfire.ext.layout.editor.client.resources.WebAppResource;
import org.uberfire.ext.layout.editor.client.structure.ColumnEditorWidget;
import org.uberfire.ext.layout.editor.client.structure.ComponentEditorWidget;
import org.uberfire.ext.layout.editor.client.structure.LayoutEditorWidget;

public class LayoutComponentView extends Composite {

    private LayoutDragComponent type;
    private ComponentEditorWidget componentEditorWidget;

    @UiField
    FluidContainer fluidContainer;

    private boolean newComponent;
    private ColumnEditorWidget parent;

    interface ScreenEditorMainViewBinder
            extends
            UiBinder<Widget, LayoutComponentView> {

    }

    private static ScreenEditorMainViewBinder uiBinder = GWT.create( ScreenEditorMainViewBinder.class );

    public LayoutComponentView( ColumnEditorWidget parent,
                                LayoutDragComponent type,
                                boolean newComponent ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.type = type;
        this.parent = parent;
        this.newComponent = newComponent;
        this.componentEditorWidget = new ComponentEditorWidget( parent, fluidContainer, type );

        update();
        if (newComponent && (type instanceof HasConfiguration)) {
            showConfigurationScreen();
        }
    }

    public LayoutComponentView( ColumnEditorWidget parent,
                                LayoutComponent component,
                                LayoutDragComponent type ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.parent = parent;
        this.type = type;
        this.componentEditorWidget = new ComponentEditorWidget( parent, fluidContainer, type );

        LayoutEditorWidget layoutEditorWidget = getLayoutEditorWidget();
        layoutEditorWidget.registerLayoutComponent(this.componentEditorWidget, component);
        update();
    }

    public ComponentEditorWidget getEditorWidget() {
        return componentEditorWidget;
    }

    public boolean isNewComponent() {
        return newComponent;
    }

    public void update() {
        componentEditorWidget.getWidget().clear();
        componentEditorWidget.getWidget().add(generateMainRow());
    }

    public void remove() {
        removeThisWidgetFromParent();
        addDropColumnPanel();
    }

    private FlowPanel generateMainRow() {
        final Panel header = generateHeaderRow();
        header.setVisible(false);

        final FlowPanel row = new FlowPanel();
        row.add(header);
        row.add(generateLayoutComponentPreview());

        row.addDomHandler(new MouseOverHandler() {
            @Override public void onMouseOver(MouseOverEvent mouseOverEvent) {
                header.setVisible(true);
                parent.getWidget().getElement().getStyle().setProperty("border", "1px solid #6DB4E1");
            }
        }, MouseOverEvent.getType());

        row.addDomHandler(new MouseOutHandler() {
            @Override public void onMouseOut(MouseOutEvent mouseOutEvent) {
                header.setVisible(false);
                parent.getWidget().getElement().getStyle().setProperty("border", "0px");
            }
        }, MouseOutEvent.getType());

        return row;
    }

    private Column generateLayoutComponentPreview() {
        LayoutEditorWidget layoutEditorWidget = getLayoutEditorWidget();
        LayoutComponent layoutComponent = layoutEditorWidget.getLayoutComponent(componentEditorWidget);
        RenderingContext renderingContext = new RenderingContext(layoutComponent, fluidContainer);
        IsWidget previewWidget = type.getPreviewWidget(renderingContext);

        Column buttonColumn = new Column(12);
        buttonColumn.getElement().getStyle().setProperty("textAlign", "left");
        if (previewWidget != null) {
            buttonColumn.add(previewWidget);
        }
        return buttonColumn;
    }

    private Column generateHeaderRow() {
        final Column header = new Column(12);
        header.getElement().getStyle().setProperty( "textAlign", "right" );
        if ( type instanceof HasConfiguration ) {
            header.add(generateConfigureButton());
        }
        header.add(generateRemoveButton());
        return header;
    }

    private Button generateConfigureButton() {
        Button remove = GWT.create( Button.class );
        remove.setSize( ButtonSize.MINI );
        remove.setType(ButtonType.PRIMARY);
        remove.setIcon(IconType.EDIT);
        remove.getElement().getStyle().setProperty("marginRight", "3px");
        remove.addClickHandler( new ClickHandler() {
            public void onClick( ClickEvent event ) {
                showConfigurationScreen();
            }
        } );
        return remove;
    }

    private void showConfigurationScreen() {
        LayoutEditorWidget layoutEditorWidget = getLayoutEditorWidget();
        LayoutComponent layoutComponent = layoutEditorWidget.getLayoutComponent(componentEditorWidget);

        if (type instanceof HasModalConfiguration) {
            ModalConfigurationContext ctx = new ModalConfigurationContext(layoutComponent, fluidContainer, this);
            Modal configModal = ((HasModalConfiguration) type).getConfigurationModal(ctx);
            configModal.show();
        }
        else if (type instanceof HasPanelConfiguration) {
            PanelConfigurationContext ctx = new PanelConfigurationContext(layoutComponent, fluidContainer, this);
            Panel configPanel = ((HasPanelConfiguration) type).getConfigurationPanel(ctx);
            componentEditorWidget.getWidget().clear();
            componentEditorWidget.getWidget().add(configPanel);
        }
    }

    private Button generateRemoveButton() {
        Button remove = GWT.create( Button.class );
        remove.setSize( ButtonSize.MINI );
        remove.setType(ButtonType.DANGER);
        remove.setIcon(IconType.REMOVE);
        remove.getElement().getStyle().setProperty("marginRight", "3px");
        remove.addClickHandler( new ClickHandler() {
            @Override
            public void onClick( ClickEvent event ) {
                remove();
            }
        } );
        return remove;
    }

    private void removeThisWidgetFromParent() {
        parent.getWidget().remove(this);
        componentEditorWidget.removeFromParent();
    }

    private void addDropColumnPanel() {
        parent.getWidget().add( new DropColumnPanel(parent) );
    }

    protected LayoutEditorWidget getLayoutEditorWidget() {
        SyncBeanManager beanManager = IOC.getBeanManager();
        IOCBeanDef<LayoutEditorWidget> layoutEditorUIIOCBeanDef = beanManager.lookupBean( LayoutEditorWidget.class );
        return layoutEditorUIIOCBeanDef.getInstance();
    }

}
