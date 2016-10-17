package org.matrix.framework.core.search;

import io.searchbox.client.JestResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.IdsQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.matrix.framework.core.page.PageModel;
import org.matrix.framework.core.platform.exception.BusinessProcessException;

import com.google.gson.JsonObject;

public class JestHelper {

    private QueryBuilder queryBuilder;

    private JestResultConvert jestResultConvert;

    public QueryBuilder getQueryBuilder() {
        if (null == queryBuilder)
            queryBuilder = new QueryBuilder();
        return queryBuilder;
    }

    public JestResultConvert getJestResultConvert() {
        if (null == jestResultConvert)
            jestResultConvert = new JestResultConvert();
        return jestResultConvert;
    }

    public static class QueryBuilder {

        public String createMultiFieldKeyQuery(String key, String[] fieldNames) {
            BoolQueryBuilder query = QueryBuilders.boolQuery();
            String[] keywords = key.replaceAll("\\s+", " ").split(" ");
            for (String fieldName : fieldNames) {
                for (String keyword : keywords) {
                    // TODO
                    // query = query.should(QueryBuilders.fieldQuery(fieldName,
                    // keyword));
                }
            }
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(query);
            return searchSourceBuilder.toString();
        }

        public String createHighPrecisionQuery(String keyWord, String[] fieldNames) {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            BoolQueryBuilder query = QueryBuilders.boolQuery();
            String[] keywords = keyWord.replaceAll("\\s+", " ").split(" ");
            for (String fieldName : fieldNames) {
                query = query.should(QueryBuilders.termsQuery(fieldName, keywords).minimumShouldMatch(String.valueOf(keywords.length)));
            }
            searchSourceBuilder.query(query);
            return searchSourceBuilder.toString();
        }

        public String createCombineQuery(List<SearchCriterion> criteria, List<RangeSearchCriterion> rangeCriteria) {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            BoolQueryBuilder query = QueryBuilders.boolQuery();
            for (RangeSearchCriterion criterion : rangeCriteria) {
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(criterion.getField());
                if (null != criterion.getFrom()) {
                    rangeQueryBuilder.gte(criterion.getFrom());
                }
                if (null != criterion.getTo()) {
                    rangeQueryBuilder.lte(criterion.getTo());
                }
                query.must(rangeQueryBuilder);
            }
            BoolQueryBuilder mustQuery = QueryBuilders.boolQuery();
            Map<String, String> map = new HashMap<String, String>();
            for (SearchCriterion criterion : criteria) {
                String value = map.get(criterion.getField());
                if (StringUtils.isNotBlank(value)) {
                    value = value + "|" + criterion.getKeyWord();
                    map.put(criterion.getField(), value);
                } else {
                    map.put(criterion.getField(), criterion.getKeyWord());
                }
            }
            for (Map.Entry<String, String> entry : map.entrySet()) {
                BoolQueryBuilder shouldQuery = QueryBuilders.boolQuery();
                String[] keyWords = entry.getValue().split("[|]");
                for (String keyWord : keyWords) {
                    String[] keys = keyWord.replaceAll("\\s+", " ").split(" ");
                    for (String key : keys) {
                        // TODO
                        // shouldQuery =
                        // shouldQuery.should(QueryBuilders.fieldQuery(entry.getKey(),
                        // key));
                    }
                }
                mustQuery.must(shouldQuery);
            }
            query.must(mustQuery);
            return searchSourceBuilder.query(query).toString();
        }

        public String createMultiFieldQuery(String key, Map<String, Float> fieldsMap) {
            BoolQueryBuilder query = QueryBuilders.boolQuery();
            String[] keywords = key.replaceAll("\\s+", " ").split(" ");
            String[] fieldNames = fieldsMap.keySet().toArray(new String[fieldsMap.size()]);
            for (String fieldName : fieldNames) {
                for (String keyword : keywords) {
                    if (!StringUtils.isNotBlank(keyword))
                        continue;
                    // TODO
                    // query = query.should(QueryBuilders.fieldQuery(fieldName,
                    // keyword).boost(fieldsMap.get(fieldName))).should(QueryBuilders.fieldQuery(fieldName,
                    // "*" + keyword + "*").boost(fieldsMap.get(fieldName)));
                    // .fuzzyLikeThisFieldQuery(fieldName).likeText(keyword).boost(fieldsMap.get(fieldName)));
                }
            }
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("title");
            searchSourceBuilder.query(query).highlight(highlightBuilder);
            return searchSourceBuilder.toString();
        }

        public String createFieldPrefixKeyQuery(String keyWord, String field) {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchPhrasePrefixQuery(field, keyWord));
            return searchSourceBuilder.toString();
        }

        public String createBoolQuery(String key, String[] fieldNames, boolean isFuzzy) {
            BoolQueryBuilder query = QueryBuilders.boolQuery();
            String[] keywords = key.replaceAll("\\s+", " ").split(" ");
            for (String fieldName : fieldNames) {
                for (String keyword : keywords) {
                    // TODO
                    // query = query.should(QueryBuilders.fieldQuery(fieldName,
                    // isFuzzy ? ("*" + keyword + "*") : keyword));
                }
            }
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(query);
            return searchSourceBuilder.toString();
        }

        /**
         * 创建指定indexType下的keyWord(必须具有id属性)查询
         */
        public String createIdQuery(String id, String indexType) {
            IdsQueryBuilder idQuery = QueryBuilders.idsQuery(indexType).addIds(id);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(idQuery);
            return searchSourceBuilder.toString();
        }

        public String createMatchAllQuery(String[] keyWords, String[] fieldName) {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            if (!StringUtils.isNotBlank(keyWords[0]) && keyWords.length == 1) {
                MatchAllQueryBuilder matchAllQuery = QueryBuilders.matchAllQuery();
                searchSourceBuilder.query(matchAllQuery);
                return searchSourceBuilder.toString();
            } else {
                BoolQueryBuilder queryError = QueryBuilders.boolQuery();
                BoolQueryBuilder query = QueryBuilders.boolQuery();
                if (keyWords[0] != null && !"".equals(keyWords[0])) {
                    // TODO
                    // queryError.should(QueryBuilders.fieldQuery(fieldName[0],
                    // keyWords[0]));
                    query.must(queryError);
                }
                if (keyWords.length == 2 && fieldName.length == 2) {
                    if (StringUtils.isNotBlank(keyWords[1]) && StringUtils.isNotBlank(fieldName[1])) {
                        BoolQueryBuilder queryRight = QueryBuilders.boolQuery();
                        // TODO
                        // queryRight.should(QueryBuilders.fieldQuery(fieldName[1],
                        // keyWords[1]));
                        query.must(queryRight);
                    }
                }
                searchSourceBuilder.query(query);
                return searchSourceBuilder.toString();
            }
        }
    }

    public static class JestResultConvert {
        public <T> T convert(JestResult result, Class<T> type) {
            if (result == null || StringUtils.isNotBlank(result.getErrorMessage()))
                throw new BusinessProcessException("搜索出错了！");
            if (!result.isSucceeded())
                throw new BusinessProcessException("查询失败！");
            return result.getSourceAsObject(type);
        }

        public <T> List<T> convertList(JestResult result, Class<T> type) {
            if (result == null || StringUtils.isNotBlank(result.getErrorMessage()))
                throw new BusinessProcessException("搜索出错了！ [" + (result == null ? "" : result.getErrorMessage()) + "]");
            if (!result.isSucceeded())
                throw new BusinessProcessException("查询失败！");
            return result.getSourceAsObjectList(type);
        }

        public <T> PageModel<T> convert(JestResult result, Integer pageNo, Integer pageSize, Class<T> type) {
            if (result == null || StringUtils.isNotBlank(result.getErrorMessage()))
                throw new BusinessProcessException("搜索出错了！" + result == null ? "" : result.getErrorMessage());
            if (!result.isSucceeded())
                throw new BusinessProcessException("查询失败！");
            PageModel<T> pageModel = new PageModel<T>();
            pageModel.setPageNo(pageNo);
            pageModel.setPageSize(pageSize);
            JsonObject res = result.getJsonObject().get("hits").getAsJsonObject();
            pageModel.setTotalRecords(res.get("total").getAsLong());
            if (pageModel.getTotalRecords() > 0)
                pageModel.setRecords(result.getSourceAsObjectList(type));
            return pageModel;
        }

        public boolean validate(JestResult result, String indexName) {
            if (result == null || StringUtils.isNotBlank(result.getErrorMessage()))
                throw new BusinessProcessException("搜索出错了！");
            if (!result.isSucceeded())
                throw new BusinessProcessException("操作执行失败！");
            JsonObject json = result.getJsonObject().get("_indices").getAsJsonObject().get(indexName).getAsJsonObject().get("_shards").getAsJsonObject();
            return json.get("total").getAsInt() == json.get("successful").getAsInt();
        }
    }
}