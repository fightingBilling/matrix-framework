package org.matrix.framework.core.search;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.engine.DocumentMissingException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.DisMaxQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder.Operator;
import org.elasticsearch.index.query.MatchQueryBuilder.Type;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.matrix.framework.core.collection.converter.JSONConverter;
import org.matrix.framework.core.log.LoggerFactory;
import org.matrix.framework.core.platform.exception.MatrixException;
import org.matrix.framework.core.util.CollectionUtils;
import org.slf4j.Logger;

public class ESAccess {

    private final Logger logger = LoggerFactory.getLogger(ESAccess.class);

    private ElasticSearchSettings elasticSearchSettings;
    private TransportClient client;
    private JSONConverter jsonConverter;
    private ESResultConverter esResultConverter;

    @PostConstruct
    public void initialize() {
        String clusterName = elasticSearchSettings.getClusterName();
        if (StringUtils.isBlank(clusterName)) {
            throw new MatrixException("The cluster name cannot be blank!");
        }
        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", clusterName).build();
        client = new TransportClient(settings);
        client.addTransportAddresses(elasticSearchSettings.getAddresses());
    }

    @PreDestroy
    public void shutdown() {
        if (null != client) {
            client.close();
        }
    }

    /**
     * 向指定的index,type加入json数据
     * 
     * @Note 未设置id,id将自动生成.
     */
    public IndexResponse putData(String index, String type, String json) {
        IndexResponse response = client.prepareIndex(index, type).setSource(json).execute().actionGet();
        return response;
    }

    /**
     * 批量创建数据
     */
    public BulkResponse putData(String index, String type, String id, List<String> jsons) {
        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        for (String json : jsons) {
            bulkRequestBuilder.add(client.prepareIndex(index, type).setSource(json));
        }
        BulkResponse bulkResponse = bulkRequestBuilder.execute().actionGet();
        return bulkResponse;
    }

    /**
     * @Note:如果ID已经存在,则会更新.
     */
    public IndexResponse putData(String index, String type, String id, String json) {
        IndexResponse response = client.prepareIndex(index, type, id).setSource(json).execute().actionGet();
        return response;
    }

    /**
     * 获取data
     */
    public GetResponse getData(String index, String type, String id) {
        GetResponse response = client.prepareGet(index, type, id).execute().actionGet();
        return response;
    }

    /**
     * 获取文档
     */
    public <T> T getData(String index, String type, String id, Class<T> clazz) {
        GetResponse response = client.prepareGet(index, type, id).execute().actionGet();
        String sourceString = response.getSourceAsString();
        if (StringUtils.isNotBlank(sourceString)) {
            return jsonConverter.fromString(clazz, response.getSourceAsString());
        }
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            return null;
        }
    }

    /**
     * 删除文档
     */
    public DeleteResponse deleteData(String index, String type, String id) {
        return client.prepareDelete(index, type, id).execute().actionGet();
    }

    /**
     * 更新数据
     */
    public UpdateResponse updateData(String index, String type, String id, String json) {
        try {
            return client.prepareUpdate(index, type, id).setDoc(json).get();
        } catch (DocumentMissingException e) {
            logger.warn("Tried to update document: index->{},type->{},id->{}, but it doesn't exist.", new Object[] { index, type, id });
        }
        return new UpdateResponse();
    }

    /**
     * 构建基本查询
     * 
     * @TODO 设置结果的分值重计算.
     */
    private SearchRequestBuilder getSearchRequestBuilder(List<String> indices, List<String> types, SearchType searchType, Integer pageNo, Integer pageSize) {
        SearchRequestBuilder searchRequestBuilder = new SearchRequestBuilder(client);
        if (null != pageNo && null != pageSize) {
            searchRequestBuilder.setFrom((pageNo - 1) * pageSize);
            searchRequestBuilder.setSize(pageSize);
        }
        if (!CollectionUtils.isEmpty(indices)) {
            searchRequestBuilder.setIndices((String[]) indices.toArray(new String[indices.size()]));
        }
        if (!CollectionUtils.isEmpty(types)) {
            searchRequestBuilder.setTypes((String[]) types.toArray(new String[types.size()]));
        }
        if (searchType != null) {
            searchRequestBuilder.setSearchType(searchType);
        }
        return searchRequestBuilder;
    }

    /**
     * 多词查询
     * 
     * @WARNING DO NOT use DFS_QUERY_THEN_FETCH as SearchType in production environment!
     */
    public <T> List<T> matchSearch(List<String> indices, List<String> types, SearchType searchType, String name, Object text, Type type, Operator operator, String minimumShouldMatch, Integer pageNo,
            Integer pageSize, Class<T> clazz) {
        SearchRequestBuilder searchRequestBuilder = getSearchRequestBuilder(indices, types, searchType, pageNo, pageSize);
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(name, text);
        if (null != type) {
            matchQueryBuilder.type(type);
        }
        if (operator != null) {
            matchQueryBuilder.operator(operator);
        }
        if (StringUtils.isNotBlank(minimumShouldMatch)) {
            matchQueryBuilder.minimumShouldMatch(minimumShouldMatch);
        }
        searchRequestBuilder.setQuery(matchQueryBuilder);
        return esResultConverter.convertList(searchRequestBuilder.execute().actionGet(), clazz);
    }

    /**
     * 合并查询
     */
    public <T> List<T> boolSearch(List<String> indices, List<String> types, SearchType searchType, Map<String, Object> must, Map<String, Object> mustNot, List<MatchCriterion> shouldMatch,
            List<MatchCriterion> shouldMatchPhrase, String minimumShouldMatch, Integer pageNo, Integer pageSize, Class<T> clazz) {
        SearchRequestBuilder searchRequestBuilder = getSearchRequestBuilder(indices, types, searchType, pageNo, pageSize);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (!CollectionUtils.isEmpty(must)) {
            for (Entry<String, Object> entry : must.entrySet()) {
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(entry.getKey(), entry.getValue());
                boolQueryBuilder.must(matchQueryBuilder);
            }
        }
        if (!CollectionUtils.isEmpty(mustNot)) {
            for (Entry<String, Object> entry : mustNot.entrySet()) {
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(entry.getKey(), entry.getValue());
                boolQueryBuilder.mustNot(matchQueryBuilder);
            }
        }
        if (!CollectionUtils.isEmpty(shouldMatch)) {
            for (MatchCriterion matchCriterion : shouldMatch) {
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(matchCriterion.getName(), matchCriterion.getText());
                boolQueryBuilder.should(matchQueryBuilder);
                Float boost = matchCriterion.getBoost();
                if (null != boost) {
                    boolQueryBuilder.boost(boost);
                }
            }
        }
        if (!CollectionUtils.isEmpty(shouldMatchPhrase)) {
            for (MatchCriterion matchCriterion : shouldMatchPhrase) {
                MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchPhraseQuery(matchCriterion.getName(), matchCriterion.getText());
                Float boost = matchCriterion.getBoost();
                if (null != boost) {
                    boolQueryBuilder.boost(boost);
                }
                Integer slop = matchCriterion.getSlop();
                if (null != slop) {
                    matchQueryBuilder.slop(slop);
                }
                boolQueryBuilder.should(matchQueryBuilder);
            }
        }
        if (StringUtils.isNotBlank(minimumShouldMatch)) {
            boolQueryBuilder.minimumShouldMatch(minimumShouldMatch);
        }
        searchRequestBuilder.setQuery(boolQueryBuilder);
        return esResultConverter.convertList(searchRequestBuilder.execute().actionGet(), clazz);
    }

    /**
     * 返回匹配了任何查询的文档[关键字组合匹配的得分更高]
     * 
     * @param tieBreaker
     *            0~1;合理的值会靠近0;
     */
    public <T> List<T> disMaxSearch(List<String> indices, List<String> types, SearchType searchType, List<MatchCriterion> matchCriterions, Float tieBreaker, Integer pageNo, Integer pageSize,
            Class<T> clazz) {
        SearchRequestBuilder searchRequestBuilder = getSearchRequestBuilder(indices, types, searchType, pageNo, pageSize);
        DisMaxQueryBuilder disMaxQueryBuilder = QueryBuilders.disMaxQuery();
        for (MatchCriterion matchCriterion : matchCriterions) {
            MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery(matchCriterion.getName(), matchCriterion.getText());
            Float boost = matchCriterion.getBoost();
            if (null != boost) {
                matchQueryBuilder.boost(boost);
            }
            disMaxQueryBuilder.add(matchQueryBuilder);
        }
        if (null != tieBreaker) {
            disMaxQueryBuilder.tieBreaker(tieBreaker);
        }
        searchRequestBuilder.setQuery(disMaxQueryBuilder);
        return esResultConverter.convertList(searchRequestBuilder.execute().actionGet(), clazz);
    }

    /**
     * 对多个字段进行相同的查询.
     */
    public <T> List<T> multiMatchSearch(List<String> indices, List<String> types, SearchType searchType, String[] fieldNames, Object text,
            org.elasticsearch.index.query.MultiMatchQueryBuilder.Type type, Operator operator, Float tieBreaker, String minimumShouldMatch, Integer pageNo, Integer pageSize, Class<T> clazz) {
        SearchRequestBuilder searchRequestBuilder = getSearchRequestBuilder(indices, types, searchType, pageNo, pageSize);
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(text, fieldNames);
        if (null != tieBreaker) {
            multiMatchQueryBuilder.tieBreaker(tieBreaker);
        }
        if (StringUtils.isNotBlank(minimumShouldMatch)) {
            multiMatchQueryBuilder.minimumShouldMatch(minimumShouldMatch);
        }
        multiMatchQueryBuilder.type(type);
        if (null != operator) {
            multiMatchQueryBuilder.operator(operator);
        }
        searchRequestBuilder.setQuery(multiMatchQueryBuilder);
        return esResultConverter.convertList(searchRequestBuilder.execute().actionGet(), clazz);
    }

    /**
     * 短语匹配
     */
    public <T> List<T> matchPhraseSearch(List<String> indices, List<String> types, SearchType searchType, String name, Object text, Integer slop, Integer pageNo, Integer pageSize, Class<T> clazz) {
        SearchRequestBuilder searchRequestBuilder = getSearchRequestBuilder(indices, types, searchType, pageNo, pageSize);
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchPhraseQuery(name, text);
        if (null != slop) {
            matchQueryBuilder.slop(slop);
        }
        searchRequestBuilder.setQuery(matchQueryBuilder);
        return esResultConverter.convertList(searchRequestBuilder.execute().actionGet(), clazz);
    }

    /**
     * 未封装的操作可获取client自用.
     */
    public TransportClient getClient() {
        return client;
    }

    @Inject
    public void setElasticSearchSettings(ElasticSearchSettings elasticSearchSettings) {
        this.elasticSearchSettings = elasticSearchSettings;
    }

    @Inject
    public void setJsonConverter(JSONConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    @Inject
    public void setEsResultConverter(ESResultConverter esResultConverter) {
        this.esResultConverter = esResultConverter;
    }

}
