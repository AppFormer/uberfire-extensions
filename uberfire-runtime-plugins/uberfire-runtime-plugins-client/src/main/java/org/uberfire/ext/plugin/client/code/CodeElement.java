package org.uberfire.ext.plugin.client.code;

import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import org.uberfire.ext.plugin.model.CodeType;
import org.uberfire.mvp.ParameterizedCommand;

/**
 * TODO: update me
 */
public interface CodeElement {

    void addNav( final Dropdown parent,
                 final ParameterizedCommand<CodeType> onChange );

    IconType getIcon();

    CodeType getType();
}
