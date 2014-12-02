package org.uberfire.ext.perspective.editor.client.resources.i18n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface CommonConstants
        extends
        Messages {

    public static final CommonConstants INSTANCE = GWT.create( CommonConstants.class );

    String SavePerspective();

    String PerspectiveName();

    String PerspectiveNameHolder();

    String TagName();

    String AddTag();

    String TagLabel();

    String TagNameHolder();

    String InvalidPerspectiveName();

    String InvalidTagName();

    String InvalidParameterName();

    String DuplicateParameterName();

    String LoadPerspective();

    String EditHtml();

    String EditComponent();

    String Add();

    String AddNewParameter();

    String ParamKey();

    String ParamValue();
}