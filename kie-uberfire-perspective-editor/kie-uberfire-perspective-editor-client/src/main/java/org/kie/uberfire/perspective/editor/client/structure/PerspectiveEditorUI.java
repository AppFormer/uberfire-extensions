package org.kie.uberfire.perspective.editor.client.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import com.google.gwt.user.client.ui.FlowPanel;
import org.kie.uberfire.perspective.editor.client.util.PerspectiveEditorAdapter;
import org.kie.uberfire.perspective.editor.model.PerspectiveEditor;
import org.kie.uberfire.perspective.editor.model.ScreenEditor;
import org.kie.uberfire.perspective.editor.model.ScreenParameter;
import org.kie.uberfire.properties.editor.model.PropertyEditorChangeEvent;
import org.kie.uberfire.properties.editor.model.PropertyEditorFieldInfo;

@ApplicationScoped
public class PerspectiveEditorUI implements EditorWidget {

    private FlowPanel container;

    private List<EditorWidget> rowEditors = new ArrayList<EditorWidget>();

    public static String PROPERTY_EDITOR_KEY = "PerspectiveEditor";

    public Map<String, Map<String, String>> screenProperties = new HashMap<String, Map<String, String>>();

    private List<String> tags;

    private String name = "";

    public PerspectiveEditorUI() {

    }

    public void setup( FlowPanel container ) {
        this.container = container;
        this.rowEditors = new ArrayList<EditorWidget>();
        this.screenProperties = new HashMap<String, Map<String, String>>();
    }

    public FlowPanel getWidget() {
        return container;
    }

    public void addChild( EditorWidget child ) {
        rowEditors.add( child );
    }

    public FlowPanel getContainer() {
        return container;
    }

    @Override
    public void removeChild( EditorWidget editorWidget ) {
        rowEditors.remove( editorWidget );
    }

    public PerspectiveEditor toPerspectiveEditor() {
        PerspectiveEditorAdapter adapter = new PerspectiveEditorAdapter( this );
        return adapter.convertToPerspectiveEditor();
    }

    public List<EditorWidget> getRowEditors() {
        return rowEditors;
    }

    public void observeEditComponentEvent( @Observes PropertyEditorChangeEvent event ) {

        PropertyEditorFieldInfo property = event.getProperty();
        if ( property.getEventId().equalsIgnoreCase( PROPERTY_EDITOR_KEY ) ) {
            Map<String, String> screenMap = screenProperties.get( property.getKey() );
            screenMap.put( property.getLabel(), property.getCurrentStringValue() );
            screenProperties.put( property.getKey(), screenMap );
        }
    }

    public Map<String, String> getScreenProperties( String hashcode ) {
        Map<String, String> screenMap = this.screenProperties.get( hashcode );
        if ( screenMap == null ) {
            screenMap = new HashMap<String, String>();
            screenMap.put( ScreenEditor.SCREEN_NAME, " " );
        }
        this.screenProperties.put( hashcode, screenMap );
        return screenMap;
    }

    public void loadProperties( String hashcode,
                                ScreenEditor editor ) {
        Map<String, String> screenMap = this.screenProperties.get( hashcode );
        if ( screenMap == null ) {
            screenMap = new HashMap<String, String>();
        }
        final List<ScreenParameter> parameters = editor.getParameters();
        parameters.add( new ScreenParameter( ScreenEditor.SCREEN_NAME, editor.getScreenName() ) );
        for ( ScreenParameter parameter : parameters ) {
            screenMap.put( parameter.getKey(), parameter.getValue() );
        }
        this.screenProperties.put( hashcode, screenMap );
    }

    public void addParameter( String hashcode,
                              ScreenParameter parameter ) {
        Map<String, String> screenMap = this.screenProperties.get( hashcode );
        if ( screenMap == null ) {
            screenMap = new HashMap<String, String>();
        }
        screenMap.put( parameter.getKey(), parameter.getValue() );

        this.screenProperties.put( hashcode, screenMap );
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setTags( List<String> tags ) {
        if ( tags == null ) {
            tags = new ArrayList<String>();
        }
        this.tags = tags;
    }

    public List<String> getTags() {
        if ( tags == null ) {
            return new ArrayList<String>();
        }
        return tags;
    }
}
