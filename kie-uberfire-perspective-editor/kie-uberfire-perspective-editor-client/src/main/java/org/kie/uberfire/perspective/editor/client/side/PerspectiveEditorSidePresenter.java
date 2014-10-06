package org.kie.uberfire.perspective.editor.client.side;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.AccordionGroup;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.kie.uberfire.perspective.editor.client.panels.dnd.DragGridElement;
import org.kie.uberfire.perspective.editor.client.util.DragType;
import org.kie.uberfire.properties.editor.model.PropertyEditorCategory;
import org.kie.uberfire.properties.editor.model.PropertyEditorEvent;
import org.kie.uberfire.properties.editor.model.PropertyEditorFieldInfo;
import org.kie.uberfire.properties.editor.model.PropertyEditorType;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.OnOpen;

@ApplicationScoped
@WorkbenchScreen(identifier = "PerspectiveEditorSidePresenter")
public class PerspectiveEditorSidePresenter {

    public interface View extends UberView<PerspectiveEditorSidePresenter> {

        void setupMenu( AccordionGroup... accordionsGroup );
    }

    @Inject
    private View view;

    @Inject
    private PlaceManager placeManager;

    @PostConstruct
    public void init() {
    }

    @AfterInitialization
    public void loadContent() {

    }

    @OnOpen
    public void onOpen() {

        AccordionGroup gridSystem = generateGridSystem();
        AccordionGroup components = generateComponent();
        view.setupMenu( gridSystem, components );

    }

    private void setupTest() {
        PropertyEditorCategory  category = new PropertyEditorCategory( "teste" )
                .withField(new PropertyEditorFieldInfo( "teste", PropertyEditorType.TEXT ) );
        PropertyEditorEvent event  = new PropertyEditorEvent("id", category  );
    }

    private AccordionGroup generateComponent() {
        AccordionGroup accordion = new AccordionGroup();
        accordion.setHeading( "Components" );
        accordion.setIcon( IconType.FOLDER_OPEN );
        accordion.add( new DragGridElement( DragType.SCREEN, " " ) );
        return accordion;
    }

    private AccordionGroup generateGridSystem() {
        AccordionGroup accordion = new AccordionGroup();
        accordion.setHeading( "Grid System" );
        accordion.setIcon( IconType.TH );
        accordion.setDefaultOpen( true );
        accordion.add( new DragGridElement( DragType.GRID, "12" ) );
        accordion.add( new DragGridElement( DragType.GRID, "6 6" ) );
        accordion.add( new DragGridElement( DragType.GRID, "4 4 4" ) );
        return accordion;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Perspective Builder";
    }

    @WorkbenchPartView
    public UberView<PerspectiveEditorSidePresenter> getView() {
        return view;
    }

}
