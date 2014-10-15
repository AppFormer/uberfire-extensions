package org.kie.uberfire.social.activities.server;

import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.service.SocialActivitiesAPI;
import org.kie.uberfire.social.activities.service.SocialAdapter;
import org.kie.uberfire.social.activities.service.SocialAdapterRepositoryAPI;
import org.uberfire.commons.services.cdi.Startup;

@Startup
@ApplicationScoped
public class SocialActivitiesEventObserver {

    @Inject
    BeanManager beanManager;

    @Inject
    Event<SocialActivitiesEvent> socialActivitiesEvent;

    @Inject
    SocialAdapterRepositoryAPI socialAdapterRepository;

    private Map<Class, SocialAdapter> socialAdapters;

    @Inject
    private SocialActivitiesAPI socialAPI;

    @PostConstruct
    public void setup() {
        socialAdapters = socialAdapterRepository.getSocialAdapters();
    }

    public void handleSocialActivitiesEvent( @Observes SocialActivitiesEvent event ) {
        socialAPI.register( event );
    }

    public void observeAllEvents( @Observes(notifyObserver = Reception.IF_EXISTS) Object event ) {
        if (socialAdapters == null) {
            return;
        }
        for ( Map.Entry<Class, SocialAdapter> entry : socialAdapters.entrySet() ) {
            SocialAdapter adapter = entry.getValue();
            if ( adapter.shouldInterceptThisEvent( event ) ) {
                SocialActivitiesEvent socialEvent = adapter.toSocial( event );
                socialActivitiesEvent.fire( socialEvent );
            }
        }
    }
}
