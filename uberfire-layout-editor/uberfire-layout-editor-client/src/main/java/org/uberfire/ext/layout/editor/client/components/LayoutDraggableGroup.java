/*
* Copyright 2015 JBoss Inc
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.uberfire.ext.layout.editor.client.components;

import java.util.HashMap;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.AccordionGroup;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import org.uberfire.ext.layout.editor.client.dnd.DragGridElement;


public class LayoutDraggableGroup extends AccordionGroup {
    private Map<String, DragGridElement> elements = new HashMap<String, DragGridElement>(  );

    public LayoutDraggableGroup( String name ) {
        this.setIcon( IconType.FOLDER_OPEN );
        this.setHeading( name );
    }

    public void addDraggable( String id, DragGridElement gridElement ) {
        add( gridElement );
        elements.put( id, gridElement );
    }

    public void removeDraggable( String id ) {
        DragGridElement element = elements.remove( id );
        if ( element != null ) {
            remove( element );
            if (elements.isEmpty()) this.toggle();
        }
    }

}
