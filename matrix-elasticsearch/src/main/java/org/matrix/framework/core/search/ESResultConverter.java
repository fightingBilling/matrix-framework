package org.matrix.framework.core.search;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.matrix.framework.core.collection.converter.JSONConverter;

public class ESResultConverter {

    private JSONConverter jsonConverter;

    public <T> List<T> convertList(SearchResponse searchResponse, Class<T> clazz) {
        List<T> list = new ArrayList<T>();
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit searchHit : hits) {
            list.add(jsonConverter.fromString(clazz, searchHit.getSourceAsString()));
        }
        return list;
    }

    @Inject
    public void setJsonConverter(JSONConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

}
