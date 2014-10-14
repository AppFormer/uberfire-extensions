package org.kie.uberfire.apps.client.home;

import java.util.List;
import javax.enterprise.context.Dependent;

import com.github.gwtbootstrap.client.ui.Breadcrumbs;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.Thumbnails;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.kie.uberfire.apps.api.Directory;
import org.kie.uberfire.apps.client.home.components.ThumbnailApp;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class AppsHomeView extends Composite implements AppsHomePresenter.View {

    private AppsHomePresenter presenter;

    @UiField
    FlowPanel mainPanel;

    @UiField
    Breadcrumbs dirs;

    @UiField
    Thumbnails dirContent;

    interface AppsHomeViewBinder
            extends
            UiBinder<Widget, AppsHomeView> {

    }

    private static AppsHomeViewBinder uiBinder = GWT.create( AppsHomeViewBinder.class );

    @AfterInitialization
    public void initialize() {
        initWidget( uiBinder.createAndBindUi( this ) );
        configBreadCrumbs();
    }

    private void configBreadCrumbs() {
        dirs.setDivider( "/" );
    }

    @Override
    public void init( final AppsHomePresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setupBreadCrumbs( List<String> breadCrumbs ) {
        for ( String breadCrumb : breadCrumbs ) {
            dirs.add( new NavLink( breadCrumb ) );
        }
    }

    @Override
    public void setupAddDir( ParameterizedCommand<String> command ) {
        generateCreateDirThumbNail( command );
    }

    @Override
    public void setupChildsDirectories( List<Directory> childsDirectories, ParameterizedCommand<String> clickCommand ) {
        dirContent.clear();
        for ( Directory childsDirectory : childsDirectories ) {
            final ThumbnailApp link = new ThumbnailApp( childsDirectory.getName(), IconType.FOLDER_OPEN, clickCommand );
            dirContent.add( link );
        }
    }

    @Override
    public void clear() {
        dirContent.clear();
    }

    private void generateCreateDirThumbNail( ParameterizedCommand<String> clickCommand ) {
        final ThumbnailApp link = new ThumbnailApp( IconType.PLUS_SIGN, clickCommand );
        dirContent.add( link );
    }

}
