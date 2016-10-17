package org.matrix.framework.core.platform.im4j.domain;

import java.util.ArrayList;
import java.util.List;

public class Im4jBulkRequest {

    private final List<Im4jRequest> im4jBulkRequest = new ArrayList<Im4jRequest>();

    public List<Im4jRequest> getBulkRequest() {
        return im4jBulkRequest;
    }

}
