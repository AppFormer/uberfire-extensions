package org.uberfire.ext.plugin.client.perspective.editor.generator;

import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManagerImpl;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.ext.layout.editor.api.LayoutServices;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;
import org.uberfire.ext.layout.editor.client.generator.LayoutGenerator;
import org.uberfire.ext.plugin.model.LayoutEditorModel;
import org.uberfire.ext.plugin.model.PluginType;
import org.uberfire.ext.plugin.service.PluginServices;

import static org.jboss.errai.ioc.client.QualifierUtil.*;

@ApplicationScoped
public class PerspectiveEditorGenerator {

    private SyncBeanManagerImpl beanManager;
    private ActivityBeansCache activityBeansCache;

    @Inject
    private LayoutGenerator layoutGenerator;

    @Inject
    private Caller<PluginServices> pluginServices;

    @Inject
    private Caller<LayoutServices> layoutServices;

    @PostConstruct
    public void setup() {
        beanManager = (SyncBeanManagerImpl) IOC.getBeanManager();
        activityBeansCache = beanManager.lookupBean( ActivityBeansCache.class ).getInstance();
    }

    @AfterInitialization
    public void loadPerspectives() {
        pluginServices.call( new RemoteCallback<Collection<LayoutEditorModel>>() {
            @Override
            public void callback( final Collection<LayoutEditorModel> response ) {
                for ( LayoutEditorModel layoutEditorModel : response ) {
                    generatePerspective( layoutEditorModel );
                }
            }
        } ).listLayoutEditor( PluginType.PERSPECTIVE_LAYOUT );
    }

    private void generatePerspective( LayoutEditorModel model ) {

        layoutServices.call( new RemoteCallback<LayoutTemplate>() {
            @Override
            public void callback( final LayoutTemplate perspective ) {
                if ( perspective != null ) {
                    generate( perspective );
                }
            }
        } ).convertLayoutFromString( model.getLayoutEditorModel() );

    }

    public void generate( LayoutTemplate layoutTemplate) {
        if ( isANewPerspective(layoutTemplate) ) {
            PerspectiveEditorScreenActivity screen = createNewScreen(layoutTemplate);
            createNewPerspective(layoutTemplate, screen );
        } else {
            PerspectiveEditorScreenActivity screen = updateScreen(layoutTemplate);
            updatePerspective(layoutTemplate, screen );
        }
    }

    private void updatePerspective( LayoutTemplate layoutTemplate,
                                    PerspectiveEditorScreenActivity screen ) {
        final IOCBeanDef<Activity> activity = activityBeansCache.getActivity(layoutTemplate.getName());
        final PerspectiveEditorActivity perspectiveEditorActivity = (PerspectiveEditorActivity) activity.getInstance();
        perspectiveEditorActivity.update( layoutTemplate, screen );
    }

    private PerspectiveEditorScreenActivity updateScreen( LayoutTemplate layoutTemplate ) {
        final IOCBeanDef<Activity> activity = activityBeansCache.getActivity( layoutTemplate.getName() + PerspectiveEditorScreenActivity.screenSufix() );
        final PerspectiveEditorScreenActivity screenActivity = (PerspectiveEditorScreenActivity) activity.getInstance();
        screenActivity.setLayoutTemplate( layoutTemplate );
        return screenActivity;
    }

    private void createNewPerspective( LayoutTemplate perspective,
                                       PerspectiveEditorScreenActivity screen ) {
        final PerspectiveEditorActivity activity = new PerspectiveEditorActivity( perspective, screen );

        beanManager.addBean( (Class) PerspectiveActivity.class, PerspectiveEditorActivity.class, null, activity, DEFAULT_QUALIFIERS, perspective.getName(), true, null );

        activityBeansCache.addNewPerspectiveActivity( beanManager.lookupBeans( perspective.getName() ).iterator().next() );

    }

    private PerspectiveEditorScreenActivity createNewScreen( LayoutTemplate perspective ) {
        PerspectiveEditorScreenActivity activity = new PerspectiveEditorScreenActivity( perspective, layoutGenerator );

        beanManager.addBean( (Class) Activity.class, PerspectiveEditorScreenActivity.class, null, activity, DEFAULT_QUALIFIERS, activity.getIdentifier(), true, null );
        beanManager.addBean( (Class) WorkbenchScreenActivity.class, PerspectiveEditorScreenActivity.class, null, activity, DEFAULT_QUALIFIERS, activity.getIdentifier(), true, null );
        beanManager.addBean( (Class) PerspectiveEditorScreenActivity.class, PerspectiveEditorScreenActivity.class, null, activity, DEFAULT_QUALIFIERS, activity.getIdentifier(), true, null );

        activityBeansCache.addNewScreenActivity( beanManager.lookupBeans( activity.getIdentifier() ).iterator().next() );
        return activity;
    }

    private boolean isANewPerspective( LayoutTemplate layoutTemplate ) {
        final IOCBeanDef<Activity> activity = activityBeansCache.getActivity(layoutTemplate.getName());
        return activity == null;
    }

}
