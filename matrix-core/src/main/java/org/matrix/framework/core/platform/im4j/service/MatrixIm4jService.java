package org.matrix.framework.core.platform.im4j.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.matrix.framework.core.platform.im4j.domain.ErrorSpecific;
import org.matrix.framework.core.platform.im4j.domain.Im4jBulkRequest;
import org.matrix.framework.core.platform.im4j.domain.Im4jRequest;
import org.matrix.framework.core.platform.im4j.domain.Im4jResult;
import org.matrix.framework.core.platform.im4j.domain.ImgSpecific;
import org.matrix.framework.core.platform.im4j.service.manager.ThumbnailManager;
import org.springframework.util.CollectionUtils;

public class MatrixIm4jService {

    private ThumbnailManager thumbnailManager;

    public List<Im4jResult> thumbnailImage(Im4jBulkRequest im4jBulkRequest) {
        final List<Im4jResult> errorBulkResponse = new ArrayList<Im4jResult>();
        List<Im4jRequest> im4jRequests = im4jBulkRequest.getBulkRequest();
        //TODO: 这里考虑用多线程来处理
        for (Im4jRequest im4jRequest : im4jRequests) {
            final Im4jResult errorResponse = new Im4jResult();
            List<ErrorSpecific> errorSpecifics = new ArrayList<ErrorSpecific>();

            final String originalImgPath = im4jRequest.getOriginalImgPath();

            final String originalImgPrefix = im4jRequest.getOriginalImgPrefix();

            final String originalImgSuffix = im4jRequest.getOriginalImgSuffix();

            errorResponse.setOriginalImgPath(originalImgPath);
            double imgPdi = im4jRequest.getPdi();
            List<ImgSpecific> imgSpecifics = im4jRequest.getImgSpecifics();
            for (ImgSpecific imgSpecific : imgSpecifics) {
                try {
                    final String destImgPath = originalImgPrefix + "_" + imgSpecific.getImgType() + "." + originalImgSuffix;
                    long width = imgSpecific.getWidth();
                    long height = imgSpecific.getHeight();
                    thumbnailManager.getThumbnailStrategy().thumbnailImmage(originalImgPath, imgPdi, width, height, destImgPath);
                } catch (Exception e) {
                    ErrorSpecific errorSpecific = new ErrorSpecific(e.getMessage(), imgSpecific);
                    errorSpecifics.add(errorSpecific);
                    errorResponse.setErrorSpecific(errorSpecifics);
                }
            }
            if (!CollectionUtils.isEmpty(errorSpecifics)) {
                errorBulkResponse.add(errorResponse);
            }
        }
        return errorBulkResponse;
    }

    @Inject
    public void setThumbnailManager(ThumbnailManager thumbnailManager) {
        this.thumbnailManager = thumbnailManager;
    }

}
