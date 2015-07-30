package org.uberfire.ext.layout.editor.client.components;

import javax.enterprise.event.Event;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.AlternateSize;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.user.client.ui.IsWidget;
import org.uberfire.ext.layout.editor.client.dnd.GridValueValidator;
import org.uberfire.workbench.events.NotificationEvent;

public class GridLayoutDragComponent extends InternalDragComponent {

    private final Event<NotificationEvent> ufNotification;

    private String span;

    public GridLayoutDragComponent( String span,
                                    Event<NotificationEvent> ufNotification ) {
        this.span = span;
        this.ufNotification = ufNotification;
    }

    public String label() {
        return span;
    }

    @Override
    public IsWidget getDragWidget() {
        final TextBox textBox = GWT.create( TextBox.class );
        textBox.setText( span );
        textBox.addBlurHandler( new BlurHandler() {
            @Override
            public void onBlur( BlurEvent event ) {
                GridValueValidator grid = new GridValueValidator();
                if ( !grid.isValid( textBox.getText() ) ) {
                    ufNotification.fire( new NotificationEvent( grid.getValidationError(), NotificationEvent.NotificationType.ERROR ) );
                    returnToOldValue( span, textBox );
                } else {
                    updateValue( textBox );
                }
            }
        } );
        textBox.setAlternateSize( AlternateSize.SMALL );
        return textBox;
    }

    private void updateValue( TextBox textBox ) {
        this.span = textBox.getText();
    }

    private void returnToOldValue( String V,
                                   TextBox textBox ) {
        textBox.setText( V );
    }
}
