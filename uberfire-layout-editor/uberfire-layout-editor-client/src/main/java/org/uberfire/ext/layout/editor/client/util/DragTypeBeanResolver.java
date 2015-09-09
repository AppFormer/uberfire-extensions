package org.uberfire.ext.layout.editor.client.util;

import java.util.Collection;

import org.jboss.errai.ioc.client.container.IOC;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManagerImpl;
import org.uberfire.ext.layout.editor.client.components.LayoutDragComponent;

public class DragTypeBeanResolver {

    public LayoutDragComponent lookupDragTypeBean( String dragTypeClassName ) {
        SyncBeanManagerImpl beanManager = (SyncBeanManagerImpl) IOC.getBeanManager();
        Collection<IOCBeanDef<LayoutDragComponent>> iocBeanDefs = beanManager.lookupBeans( LayoutDragComponent.class );

        for ( IOCBeanDef<LayoutDragComponent> iocBeanDef : iocBeanDefs ) {
            final LayoutDragComponent instance = iocBeanDef.getInstance();
            if ( instance.getClass().getName().equalsIgnoreCase( dragTypeClassName ) ) {
                return instance;
            }
        }
        return null;
    }

}
