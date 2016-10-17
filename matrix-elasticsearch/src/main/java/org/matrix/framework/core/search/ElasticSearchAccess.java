package org.matrix.framework.core.search;

import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.config.HttpClientConfig.Builder;
import io.searchbox.core.Bulk;
import io.searchbox.core.Delete;
import io.searchbox.core.DeleteByQuery;
import io.searchbox.core.Doc;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.MultiGet;
import io.searchbox.core.Search;
import io.searchbox.core.search.sort.Sort;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.IndicesExists;
import io.searchbox.indices.mapping.PutMapping;
import io.searchbox.params.SearchType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.matrix.framework.core.page.PageModel;
import org.matrix.framework.core.platform.exception.BusinessProcessException;

public class ElasticSearchAccess extends JestHelper {

    private JestClient elasticSearchClient;
    private ElasticSearchSettings elasticSearchSettings;

    @PostConstruct
    public void initialize() {
        HttpClientConfig config = new HttpClientConfig(new Builder(Arrays.asList(elasticSearchSettings.getJestServers())).multiThreaded(true).discoveryEnabled(true).connTimeout(5000)
                .readTimeout(5000).discoveryFrequency(elasticSearchSettings.getDiscoveryFrequency(), TimeUnit.MINUTES));
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(config);
        elasticSearchClient = factory.getObject();
    }

    @PreDestroy
    public void shutdown() {
        if (null != elasticSearchClient) {
            elasticSearchClient.shutdownClient();
        }
    }

    /**
     * indexType为空时,删除索引.indexType不为空时,删除indexType
     */
    public void deleteIndex(String indexName, String indexType) throws Exception {
        DeleteIndex.Builder deleteBuilder = new DeleteIndex.Builder(indexName);
        if (null != indexType) {
            deleteBuilder.type(indexType);
        }
        elasticSearchClient.execute(deleteBuilder.build());
    }

    /**
     * 检查索引是否存在
     */
    public boolean checkIndexExists(String indexName) throws Exception {
        IndicesExists.Builder existsBuilder = new IndicesExists.Builder(indexName);
        JestResult result = elasticSearchClient.execute(existsBuilder.build());
        return result.isSucceeded();
    }

    /**
     * 重建indexName(先刪除原indexName,再新建indexName)
     */
    public void createEmptyIndex(String indexName, String indexType, Map<String, String> settings) throws Exception {
        deleteIndex(indexName, null);
        CreateIndex.Builder createBuilder = new CreateIndex.Builder(indexName);
        if (null != settings && !settings.isEmpty()) {
            createBuilder.settings(settings);
        }
        elasticSearchClient.execute(createBuilder.build());
    }

    /**
     * 重建indexName下的indexType并为其批量创建数据.使用此API之前,建议指定的indexName已经存在.
     * 如果没有指定的indexName,用createEmptyIndex方法创建.
     */
    public void createIndex(List<?> data, String indexName, String indexType, Object mappingObj) throws Exception {
        if (data == null || data.isEmpty())
            return;
        deleteIndex(indexName, indexType);
        createMapping(indexName, indexType, mappingObj);
        bulkPutData(data, indexName, indexType);
    }

    /**
     * 为指定的indexName下的indexType创建mapping
     */
    public void createMapping(String indexName, String indexType, Object mappingObj) throws Exception {
        PutMapping putMapping = new PutMapping.Builder(indexName, indexType, mappingObj).build();
        JestResult result = elasticSearchClient.execute(putMapping);
        if (result == null)
            throw new BusinessProcessException("Create mapping failed.");
    }

    /**
     * 往指定indexName下的indexType内put一条数据
     * 
     * @param data
     *            该类型可为JSON,POJO,Map(LinkedHashMap).参见ElasticSearchAccessTest
     *            为POJO时可通过注解@JestId注明id,而另两种方式方式需注明id,否则会使用自动生成的id.
     */
    public void putData(Object data, String indexName, String indexType, String indexId) throws Exception {
        Index.Builder indexBuilder = new Index.Builder(data).index(indexName).type(indexType);
        if (StringUtils.isNotBlank(indexId)) {
            indexBuilder.id(indexId);
        }
        elasticSearchClient.execute(indexBuilder.build());
    }

    /**
     * 批量创建数据
     */
    public void bulkPutData(List<?> data, String indexName, String indexType) throws Exception {
        List<Index> actions = new ArrayList<Index>();
        Index action = null;
        for (Object obj : data) {
            action = new Index.Builder(obj).index(indexName).type(indexType).build();
            actions.add(action);
        }
        Bulk bulk = new Bulk.Builder().defaultIndex(indexName).defaultType(indexType).addAction(actions).build();
        JestResult result = elasticSearchClient.execute(bulk);
        if (!result.isSucceeded()) {
            throw new BusinessProcessException("添加数据失败!");
        }
    }

    /**
     * 至多查询两组keyWord-fieldName
     */
    public <T> PageModel<T> query(String[] keyWords, String[] fieldName, String indexName, String indexType, Class<T> type, Integer pageNo, Integer pageSize) throws Exception {
        Search.Builder search = new Search.Builder(getQueryBuilder().createMatchAllQuery(keyWords, fieldName));
        search = search.addIndex(indexName).addType(indexType);
        search = search.setParameter("from", (pageNo - 1) * pageSize).setParameter("size", pageSize);
        return getJestResultConvert().convert(elasticSearchClient.execute(search.build()), pageNo, pageSize, type);
    }

    /**
     * 至多查询两组keyWord-fieldName,带排序
     */
    public <T> PageModel<T> query(String[] keyWords, String[] fieldName, List<Sort> sorts, String indexName, String indexType, Class<T> type, Integer pageNo, Integer pageSize) throws Exception {
        Search.Builder search = new Search.Builder(getQueryBuilder().createMatchAllQuery(keyWords, fieldName));
        search = search.addIndex(indexName).addType(indexType);
        if (sorts != null && !sorts.isEmpty())
            search = search.addSort(sorts);
        search = search.setParameter("from", (pageNo - 1) * pageSize).setParameter("size", pageSize);
        return getJestResultConvert().convert(elasticSearchClient.execute(search.build()), pageNo, pageSize, type);
    }

    /**
     * 查询指定indexName下indexType中,id为keyWord的document.与getDocument()功能相同.
     * 该方法用的是elasticSearch中的方法.
     */
    @Deprecated
    public <T> T query(String id, String indexName, String indexType, Class<T> type) throws Exception {
        if (!checkIndexExists(indexName)) {
            return null;
        }
        Search.Builder search = new Search.Builder(getQueryBuilder().createIdQuery(id, indexType));
        search = search.addIndex(indexName).addType(indexType);
        return getJestResultConvert().convert(elasticSearchClient.execute(search.build()), type);
    }

    public <T> List<T> query(String keyWork, Map<String, Float> fieldsMap, List<Sort> sorts, String indexName, String indexType, Class<T> type, boolean isFuzzy) throws Exception {
        Search.Builder search = new Search.Builder(getQueryBuilder().createMultiFieldQuery(keyWork, fieldsMap));
        search = search.addIndex(indexName).addType(indexType).setSearchType(SearchType.QUERY_THEN_FETCH);
        if (sorts != null && !sorts.isEmpty())
            search = search.addSort(sorts);
        return getJestResultConvert().convertList(elasticSearchClient.execute(search.build()), type);
    }

    /**
     * 将同一keyWord放到多个field中进行查询.
     */
    public <T> List<T> query(String keyWord, String[] fieldNames, String indexName, String indexType, Class<T> type) throws Exception {
        Search.Builder search = new Search.Builder(getQueryBuilder().createMultiFieldKeyQuery(keyWord, fieldNames));
        search = search.addIndex(indexName).addType(indexType).setParameter("from", 0).setParameter("size", 10);
        return getJestResultConvert().convertList(elasticSearchClient.execute(search.build()), type);
    }

    /**
     * prefixQuery不会将keyWord作分词处理,且将keyWord作为前缀(或后缀?)进行查询
     */
    public <T> List<T> prefixQuery(String keyWord, String field, String indexName, String indexType, Class<T> type) throws Exception {
        Search.Builder search = new Search.Builder(getQueryBuilder().createFieldPrefixKeyQuery(keyWord, field));
        search = search.addIndex(indexName).addType(indexType).setParameter("from", 0).setParameter("size", 10);
        return getJestResultConvert().convertList(elasticSearchClient.execute(search.build()), type);
    }

    public <T> PageModel<T> query(String keyWork, Map<String, Float> fieldsMap, List<Sort> sorts, String indexName, String indexType, Class<T> type, boolean isFuzzy, Integer pageNo, Integer pageSize)
            throws Exception {
        Search.Builder search = new Search.Builder(getQueryBuilder().createMultiFieldQuery(keyWork, fieldsMap));
        search = search.addIndex(indexName).addType(indexType);
        if (sorts != null && !sorts.isEmpty())
            search = search.addSort(sorts);
        search = search.setParameter("from", (pageNo - 1) * pageSize).setParameter("size", pageSize);
        return getJestResultConvert().convert(elasticSearchClient.execute(search.build()), pageNo, pageSize, type);
    }

    public boolean delete(String keyWork, String[] fieldName, String indexName, String indexType, boolean isFuzzy) throws Exception {
        DeleteByQuery query = new DeleteByQuery.Builder(getQueryBuilder().createBoolQuery(keyWork, fieldName, isFuzzy)).addIndex(indexName).addType(indexType).build();
        return getJestResultConvert().validate(elasticSearchClient.execute(query), indexName);
    }

    /**
     * 查询出满足fieldName1,keyWord1组合或者fieldName2,keyWord2组合的结果
     */
    public <T> List<T> booleanQuery(String fieldName1, String fieldName2, String keyWord1, String keyWord2, Class<T> type, String indexName, String indexType) throws Exception {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.boolQuery()//
                .should(QueryBuilders.matchQuery(fieldName1, keyWord1))//
                .should(QueryBuilders.matchQuery(fieldName2, keyWord2))//
                );
        Search.Builder builder = new Search.Builder(searchSourceBuilder.toString()).addIndex(indexName).addType(indexType);
        JestResult result = elasticSearchClient.execute(builder.build());
        return getJestResultConvert().convertList(result, type);
    }

    /**
     * 查询出满足[0] && ([1] || [2])的结果
     */
    public <T> List<T> nestedBooleanQuery(String[] fieldNames, String[] keyWords, Class<T> type) throws Exception {
        BoolQueryBuilder matchOnAnyOneOfTheseFields = QueryBuilders.boolQuery()//
                .should(QueryBuilders.matchQuery(fieldNames[1], keyWords[1]))//
                .should(QueryBuilders.matchQuery(fieldNames[2], keyWords[2]));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(//
                QueryBuilders.boolQuery()//
                        .must(QueryBuilders.matchQuery(fieldNames[0], keyWords[0]))//
                        .must(matchOnAnyOneOfTheseFields)//
                );
        Search search = new Search.Builder(searchSourceBuilder.toString()).build();
        return getJestResultConvert().convertList(elasticSearchClient.execute(search), type);
    }

    /**
     * 得到指定的document
     */
    public <T> T getDocument(String indexName, String indexType, String indexId, Class<T> type) throws Exception {
        Get get = new Get.Builder(indexName, indexId).type(indexType).build();
        return getJestResultConvert().convert(elasticSearchClient.execute(get), type);
    }

    /**
     * 批量得到document,doc中指定indexName,indexType,id.只会返回查询到的结果
     */
    public <T> List<T> multiGet(List<Doc> docs, Class<T> type) throws Exception {
        GenericResultAbstractAction multiGet = new MultiGet.Builder.ByDoc(docs).build();
        return getJestResultConvert().convertList(elasticSearchClient.execute(multiGet), type);
    }

    /**
     * 删除指定indexId的document
     */
    public boolean delete(String indexId, String indexName, String indexType) throws Exception {
        Delete.Builder deleteBuilder = new Delete.Builder(indexId);
        return elasticSearchClient.execute(deleteBuilder.build()).isSucceeded();
    }

    /**
     * 更新指定indexName,indexType,indexId的document(先根据id刪除原有document,再根据提供的数据新建)
     */
    public void updateDocument(String indexName, String indexType, String indexId, Object data) throws Exception {
        Bulk bulk = new Bulk.Builder().defaultIndex(indexName).defaultType(indexType)//
                .addAction(new Delete.Builder(indexId).build())//
                .addAction(new Index.Builder(data).index(indexName).type(indexType).id(indexId).build())//
                .build();
        elasticSearchClient.execute(bulk);
    }

    /**
     * termsQuery不会将term(keyWord)作分词处理.将严格按照term进行查询.多个term将会进一步提高搜索的精度.
     */
    public <T> List<T> termsQuery(String field, String[] terms, String indexName, String indexType, Class<T> type) throws Exception {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termsQuery(field, terms).minimumShouldMatch(String.valueOf(terms.length)));
        Search.Builder builder = new Search.Builder(searchSourceBuilder.toString());
        builder.addIndex(indexName).addType(indexType);
        JestResult result = elasticSearchClient.execute(builder.build());
        return getJestResultConvert().convertList(result, type);
    }

    /**
     * 未证实.
     */
    public <T> List<T> moreLikeThisQuery(String likeText, String[] fields, String indexName, String indexType, Class<T> type) throws Exception {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.moreLikeThisQuery(fields).likeText(likeText));
        Search.Builder builder = new Search.Builder(searchSourceBuilder.toString());
        builder.addIndex(indexName).addType(indexType);
        JestResult result = elasticSearchClient.execute(builder.build());
        return getJestResultConvert().convertList(result, type);
    }

    /**
     * 只查询指定的fields中都匹配keyWord的结果.
     */
    public <T> PageModel<T> highPrecisionQuery(String keyWord, String[] fieldNames, String indexName, String indexType, Class<T> type, Integer pageNo, Integer pageSize) throws Exception {
        Search.Builder builder = new Search.Builder(getQueryBuilder().createHighPrecisionQuery(keyWord, fieldNames));
        builder.addIndex(indexName).addType(indexType).setParameter("from", (pageNo - 1) * pageSize).setParameter("size", pageSize);
        JestResult result = elasticSearchClient.execute(builder.build());
        return getJestResultConvert().convert(result, pageNo, pageSize, type);
    }

    /**
     * 条件加范围的复合搜索.(还有bug)
     */
    public <T> PageModel<T> combineQuery(List<SearchCriterion> criteria, List<RangeSearchCriterion> rangeCriteria, String indexName, String indexType, Class<T> type, Integer pageNo, Integer pageSize)
            throws Exception {
        Search.Builder builder = new Search.Builder(getQueryBuilder().createCombineQuery(criteria, rangeCriteria));
        builder.addIndex(indexName).addType(indexType).setParameter("from", (pageNo - 1) * pageSize).setParameter("size", pageSize);
        JestResult result = elasticSearchClient.execute(builder.build());
        return getJestResultConvert().convert(result, pageNo, pageSize, type);
    }

    @Inject
    public void setElasticSearchSettings(ElasticSearchSettings elasticSearchSettings) {
        this.elasticSearchSettings = elasticSearchSettings;
    }
}
