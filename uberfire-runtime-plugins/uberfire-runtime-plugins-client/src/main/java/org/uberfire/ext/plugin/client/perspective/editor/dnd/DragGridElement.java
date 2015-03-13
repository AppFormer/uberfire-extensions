package org.uberfire.ext.plugin.client.perspective.editor.dnd;

import javax.enterprise.event.Event;

import com.github.gwtbootstrap.client.ui.InputAddOn;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlternateSize;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.uberfire.ext.plugin.client.perspective.editor.api.ExternalPerspectiveEditorComponent;
import org.uberfire.ext.plugin.client.perspective.editor.util.DragType;
import org.uberfire.ext.plugin.client.resources.i18n.CommonConstants;
import org.uberfire.workbench.events.NotificationEvent;

public class DragGridElement extends Composite {

    private final Event<NotificationEvent> ufNotification;
    private ExternalPerspectiveEditorComponent externalComponent;
    private DragType type;
    private String dragValue;

    @UiField
    InputAddOn move;

    TextBox textBox;

    public DragGridElement( DragType type,
                            final String dragText,
                            Event<NotificationEvent> ufNotification
                          ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.ufNotification = ufNotification;
        this.dragValue = dragText;
        this.type = type;
        build();
    }

    public DragGridElement( DragType type,
                            String dragText,
                            ExternalPerspectiveEditorComponent externalPerspectiveEditorComponent ) {
        initWidget( uiBinder.createAndBindUi( this ) );
        this.ufNotification = null;
        this.dragValue = dragText;
        this.type = type;
        this.externalComponent = externalPerspectiveEditorComponent;
        build();
    }

    private void build() {
        if ( type == DragType.GRID ) {
            createTextBox();
        } else {
            createComponentWidget();
        }
        createMoveIcon( type );
    }

    private void createMoveIcon( final DragType type ) {
        move.setTitle( CommonConstants.INSTANCE.DragAndDrop() );
        move.addDomHandler( new DragStartHandler() {
            @Override
            public void onDragStart( DragStartEvent event ) {
                String text = extractText();
                event.setData( type.name(), text );
                if ( isAExternalComponent( type ) ) {
                    event.setData( type.name(), externalComponent.getClass().getName() );
                }
                event.getDataTransfer().setDragImage( move.getElement(), 10, 10 );
            }
        }, DragStartEvent.getType() );

        move.getElement().setDraggable( Element.DRAGGABLE_TRUE );
    }

    private boolean isAExternalComponent( DragType type ) {
        return type == DragType.EXTERNAL;
    }

    private String extractText() {
        return textBox.getText().isEmpty() ? type.name() : textBox.getText();
    }

    private void createComponentWidget() {
        textBox = new TextBox();
        textBox.setPlaceholder( dragValue );
        textBox.setReadOnly( true );
        textBox.setAlternateSize( AlternateSize.MEDIUM );
        move.add( textBox );
    }

    private void createTextBox() {
        textBox = new TextBox();
        textBox.setText( dragValue );
        textBox.setAlternateSize( AlternateSize.SMALL );
        textBox.addBlurHandler( new BlurHandler() {
            @Override
            public void onBlur( BlurEvent event ) {
                GridValueValidator grid = new GridValueValidator();
                if ( !grid.isValid( textBox.getText() ) ) {
                    ufNotification.fire( new NotificationEvent(grid.getValidationError(), NotificationEvent.NotificationType.ERROR ) );
                    returnToOldValue();
                }
                else{
                    updateValue();
                }
            }
        } );
        move.add( textBox );
    }

    private void updateValue() {
        dragValue = textBox.getText();
    }

    private void returnToOldValue() {
        textBox.setText( dragValue );
    }

    interface MyUiBinder extends UiBinder<Widget, DragGridElement> {

    }

    private static MyUiBinder uiBinder = GWT.create( MyUiBinder.class );

}
