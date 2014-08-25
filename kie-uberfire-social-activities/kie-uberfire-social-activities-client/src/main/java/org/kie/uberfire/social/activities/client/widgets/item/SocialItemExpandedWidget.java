package org.kie.uberfire.social.activities.client.widgets.item;

import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.FluidRow;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.NavList;
import com.github.gwtbootstrap.client.ui.Paragraph;
import com.github.gwtbootstrap.client.ui.Thumbnail;
import com.github.gwtbootstrap.client.ui.Thumbnails;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import org.kie.uberfire.social.activities.client.gravatar.GravatarBuilder;
import org.kie.uberfire.social.activities.model.SocialActivitiesEvent;
import org.kie.uberfire.social.activities.model.SocialUser;

public class SocialItemExpandedWidget {

    public static FluidRow createFirstRow( SocialActivitiesEvent event ) {
        FluidRow row = GWT.create( FluidRow.class );

        row.add( createTitle( event ) );
        row.add( createViewProcessLink() );

        return row;
    }

    private static Column createViewProcessLink() {
        Column column = new Column( 6 );
        NavList list = new NavList();
        NavLink link = new NavLink();
        link.setText( "view process" );
        list.add( link );
        column.add( list );
        return column;
    }

    private static Column createTitle( SocialActivitiesEvent event ) {
        Column column = new Column( 6 );

        StringBuilder title = new StringBuilder();
        title.append( event.getAdditionalInfo()[ 0 ] );
        title.append( ". " );
        title.append( DateTimeFormat.getFormat( DateTimeFormat.PredefinedFormat.DATE_SHORT ).format( event.getTimestamp() ) );
        column.add( new Paragraph( title.toString() ) );
        return column;
    }

    public static FluidRow createSecondRow( SocialActivitiesEvent event ) {
        FluidRow row = GWT.create( FluidRow.class );
        row.add( createThumbNail( event.getSocialUser() ) );

        row.add( createSocialUserName( event ) );

        row.add( createAdicionalInfo( event.getAdditionalInfo()[ 1 ] ) );

        return row;
    }

    private static Column createAdicionalInfo( String str ) {
        Column column;
        column = new Column( 8 );
        StringBuilder comment = new StringBuilder();
        comment.append( str );
        column.add( new Paragraph( comment.toString() ) );
        return column;
    }

    private static Column createSocialUserName( SocialActivitiesEvent event ) {
        Column column = new Column( 2 );
        NavList list = new NavList();
        NavLink link = new NavLink();
        link.setText( event.getSocialUser().getUserName() );
        list.add( link );
        column.add( list );
        return column;
    }

    private static Column createThumbNail( SocialUser socialUser ) {
        Column column = new Column( 2 );
        Thumbnails tumThumbnails = new Thumbnails();
        Thumbnail t = new Thumbnail();
        Image userImage;
        userImage = GravatarBuilder.generate( socialUser, GravatarBuilder.SIZE.SMALL );
        userImage.setSize( "30px", "30px" );
        t.add( userImage );
        tumThumbnails.add( t );
        column.add( tumThumbnails );
        return column;
    }

}
