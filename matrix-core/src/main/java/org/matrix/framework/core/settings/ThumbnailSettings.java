package org.matrix.framework.core.settings;

import org.matrix.framework.core.platform.im4j.service.manager.ThumbnailProviderType;

public class ThumbnailSettings {

    private ThumbnailProviderType thumbnailProviderType = ThumbnailProviderType.SIMPLE;

    public ThumbnailProviderType getThumbnailProviderType() {
        return thumbnailProviderType;
    }

    public void setThumbnailProviderType(ThumbnailProviderType thumbnailProviderType) {
        this.thumbnailProviderType = thumbnailProviderType;
    }

}
