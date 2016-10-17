package org.matrix.framework.core.platform.web.request;

import java.util.regex.Pattern;

import org.matrix.framework.core.platform.exception.InvalidRequestException;
import org.matrix.framework.core.util.StringUtils;

public class RequestIdValidator {
    static final Pattern PATTERN_REQUEST_ID = Pattern.compile("[a-zA-Z0-9\\-]+");
    static final int REQUEST_ID_MAX_LENGTH = 50;

    static void validateRequestId(String requestId) {
        if (!StringUtils.hasText(requestId))
            return;
        if (requestId.length() > 50)
            throw new InvalidRequestException("the max length of requestId is 50");
        if (!PATTERN_REQUEST_ID.matcher(requestId).matches())
            throw new InvalidRequestException("the requestId must match " + PATTERN_REQUEST_ID.pattern());
    }
}