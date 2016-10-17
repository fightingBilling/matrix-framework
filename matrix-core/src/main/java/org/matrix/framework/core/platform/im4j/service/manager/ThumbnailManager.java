package org.matrix.framework.core.platform.im4j.service.manager;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.matrix.framework.core.platform.SpringObjectFactory;
import org.matrix.framework.core.platform.im4j.service.strategy.Im4jThumbnailStrategy;
import org.matrix.framework.core.platform.im4j.service.strategy.SimpleThumbnailStrategy;
import org.matrix.framework.core.platform.im4j.service.strategy.ThumbnailStrategy;
import org.matrix.framework.core.settings.ThumbnailSettings;

public class ThumbnailManager {

    private SpringObjectFactory springObjectFactory;

    private ThumbnailSettings thumbnailSettings;

    private static final String BEAN_NAME_THUMBNAIL_PROVIDER = "thumbnailStrategy";

    @PostConstruct
    public void initialize() {
        ThumbnailProviderType type = thumbnailSettings.getThumbnailProviderType();
        if (ThumbnailProviderType.SIMPLE.equals(type)) {
            springObjectFactory.registerSingletonBean(BEAN_NAME_THUMBNAIL_PROVIDER, SimpleThumbnailStrategy.class);
        } else if (ThumbnailProviderType.IM4J.equals(type)) {
            springObjectFactory.registerSingletonBean(BEAN_NAME_THUMBNAIL_PROVIDER, Im4jThumbnailStrategy.class);
        } else {
            throw new IllegalStateException("unsupported the type of thumbnail strategy, type=" + type);
        }
    }

    public ThumbnailStrategy getThumbnailStrategy() {
        return springObjectFactory.getBean(ThumbnailStrategy.class);
    }

    @Inject
    public void setSpringObjectFactory(SpringObjectFactory springObjectFactory) {
        this.springObjectFactory = springObjectFactory;
    }

    @Inject
    public void setThumbnailSettings(ThumbnailSettings thumbnailSettings) {
        this.thumbnailSettings = thumbnailSettings;
    }

}
