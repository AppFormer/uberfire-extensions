package org.kie.uberfire.social.activities.client.widgets.item.model;

import java.util.List;

import com.github.gwtbootstrap.client.ui.FluidContainer;
import org.kie.uberfire.social.activities.client.widgets.timeline.regular.model.SocialTimelineWidgetModel;
import org.kie.uberfire.social.activities.client.widgets.timeline.regular.model.UpdateItem;

public class SocialItemExpandedWidgetModel {

    private final FluidContainer itemsPanel;
    private final String fileName;
    private final List<UpdateItem> updateItems;
    private final SocialTimelineWidgetModel model;

    public SocialItemExpandedWidgetModel( FluidContainer itemsPanel,
                                          String fileName,
                                          List<UpdateItem> updateItems,
                                          SocialTimelineWidgetModel model ) {
        this.itemsPanel = itemsPanel;
        this.fileName = fileName;
        this.updateItems = updateItems;
        this.model = model;
    }

    public FluidContainer getItemsPanel() {
        return itemsPanel;
    }

    public String getFileName() {
        return fileName;
    }

    public List<UpdateItem> getUpdateItems() {
        return updateItems;
    }

    public SocialTimelineWidgetModel getModel() {
        return model;
    }

}
