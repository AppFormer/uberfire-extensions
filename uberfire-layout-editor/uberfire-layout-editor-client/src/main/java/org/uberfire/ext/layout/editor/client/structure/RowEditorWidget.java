package org.uberfire.ext.layout.editor.client.structure;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.FlowPanel;
import org.uberfire.ext.layout.editor.client.components.HasOnRemoveNotification;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;

public class RowEditorWidget implements EditorWidget {

    private final EditorWidget parent;
    private final FlowPanel container;
    private List<String> rowSpans = new ArrayList<String>();

    private List<EditorWidget> columnEditors = new ArrayList<EditorWidget>();

    public RowEditorWidget(EditorWidget parent,
            FlowPanel container,
            String rowSpamString) {
        this.parent = parent;
        this.container = container;
        parseRowSpanString( rowSpamString );
        parent.addChild( this );
    }

    public RowEditorWidget(EditorWidget parent,
            FlowPanel container,
            List<String> rowSpans) {
        this.parent = parent;
        this.container = container;
        this.rowSpans = rowSpans;
        parent.addChild( this );
    }

    public EditorWidget getParent() {
        return parent;
    }

    public FlowPanel getWidget() {
        return container;
    }

    public List<String> getRowSpans() {
        return rowSpans;
    }

    private void parseRowSpanString( String rowSpamString ) {
        String[] spans = rowSpamString.split( " " );
        for ( String span : spans ) {
            rowSpans.add( span );
        }
    }

    @Override
    public List<EditorWidget> getChildren() {
        return columnEditors;
    }

    public void addChild( EditorWidget columnEditor ) {
        columnEditors.add( columnEditor );
    }

    public void removeFromParent() {
        parent.removeChild( this );

        notifyChildRemoval( columnEditors );
    }

    protected void notifyChildRemoval( List<EditorWidget> childEditors ) {
        if (childEditors == null) return;

        for (EditorWidget editor : childEditors) {

            if (editor.getType() instanceof HasOnRemoveNotification) {
                ((HasOnRemoveNotification)editor.getType()).onRemoveComponent();
            }

            if (editor.getChildren() != null) notifyChildRemoval( editor.getChildren() );
        }
    }

    @Override
    public void removeChild( EditorWidget editorWidget ) {
        columnEditors.remove( editorWidget );
    }

    public List<EditorWidget> getColumnEditors() {
        return columnEditors;
    }

    @Override
    public LayoutDragComponent getType() {
        return null;
    }

}
