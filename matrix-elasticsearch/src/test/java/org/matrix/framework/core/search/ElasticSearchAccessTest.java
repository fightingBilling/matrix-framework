package org.matrix.framework.core.search;

import io.searchbox.core.Doc;
import io.searchbox.core.search.sort.Sort;
import io.searchbox.core.search.sort.Sort.Sorting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.matrix.framework.core.json.JSONBinder;
import org.matrix.framework.core.page.PageModel;

public class ElasticSearchAccessTest {

    private ElasticSearchSettings settings;
    private ElasticSearchAccess elasticSearchAccess;
    private Integer pageNo = 1;
    private Integer pageSize = 100;
    private Class<Location> type = Location.class;

    String indexName = "matrix";
    String indexType = "location";

    @Before
    public void before() {
        String server = "http://192.168.2.119:9200";
        settings = new ElasticSearchSettings();
        settings.setJestServers(server.split(";"));
        // Server地址及端口
        settings.setDiscoveryFrequency(10L);
        elasticSearchAccess = new ElasticSearchAccess();
        elasticSearchAccess.setElasticSearchSettings(settings);
        elasticSearchAccess.initialize();
    }

    @After
    public void after() {
        elasticSearchAccess.shutdown();
    }

    @Test
    public void testCheckIndexExists() throws Exception {
        System.out.println(elasticSearchAccess.checkIndexExists("matrix"));
    }

    @Test
    public void testCreateEmptyIndex() throws Exception {
        Map<String, String> setting = null;
        elasticSearchAccess.createEmptyIndex(indexName, indexType, setting);
    }

    @Test
    public void testCreateIndex() throws Exception {
        List<Location> locationList = new ArrayList<Location>();
        Location l1 = new Location("东方希望天祥广场", 2);
        Location l2 = new Location("天府软件园", 8);
        Location l3 = new Location("天府广场", 99);
        Location l4 = new Location("软件中心", 4);
        Location l5 = new Location("未来城", 5);
        Location l6 = new Location("世纪城", 5);
        Location l7 = new Location("中信城", 5);
        Location l8 = new Location("天府新区", 1);
        Location l9 = new Location("万达广场", 7);
        Location l10 = new Location("金融中心", 4);
        Location l11 = new Location("蜀都中心", 4);
        locationList.add(l1);
        locationList.add(l2);
        locationList.add(l3);
        locationList.add(l4);
        locationList.add(l5);
        locationList.add(l6);
        locationList.add(l7);
        locationList.add(l8);
        locationList.add(l9);
        locationList.add(l10);
        locationList.add(l11);
        XContentBuilder content = XContentFactory.jsonBuilder().startObject()//
                .startObject(indexType)//
                .startObject("properties")//
                .startObject("name").field("type", "String").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()//
                .startObject("age").field("type", "Integer").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()//
                .endObject().endObject().endObject();//
        elasticSearchAccess.createIndex(locationList, indexName, indexType, content.string());
    }

    @Test
    public void testPutData1() throws Exception {
        Location location = new Location("万达", 15);
        String data = JSONBinder.binder(Location.class).toJSON(location);
        elasticSearchAccess.putData(data, indexName, indexType, "万达");
    }

    @Test
    public void testPutData2() throws Exception {
        Location data = new Location("天府广场", 30);
        elasticSearchAccess.putData(data, indexName, indexType, null);
    }

    @Test
    public void testPutData3() throws Exception {
        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("name", "万达广场");
        data.put("age", 7);
        elasticSearchAccess.putData(data, indexName, indexType, "万达广场");
    }

    @Test
    public void testBulkPutData() throws Exception {
        List<String> data = new ArrayList<String>();
        Location location1 = new Location("蜀都中心", 5);
        Location location2 = new Location("软件中心", 4);
        data.add(JSONBinder.binder(Location.class).toJSON(location1));
        data.add(JSONBinder.binder(Location.class).toJSON(location2));
        elasticSearchAccess.bulkPutData(data, indexName, indexType);
    }

    @Test
    public void testQuery1() throws Exception {
        String[] keyWords = { "天府软" };
        String[] fieldName = { "name" };
        Class<Location> type = Location.class;
        PageModel<Location> pageModel = elasticSearchAccess.query(keyWords, fieldName, indexName, indexType, type, pageNo, pageSize);
        System.out.println("Got records：" + pageModel.getTotalRecords());
        printLocations(pageModel.getRecords());
    }

    @Test
    public void testQuery2() throws Exception {
        String[] keyWord = { "中心", "4", "" };
        String[] fieldName = { "name", "age", "age" };
        // 附加排序条件的查询
        List<Sort> sorts = new ArrayList<Sort>();
        sorts.add(new Sort("age", Sorting.DESC));
        PageModel<Location> pageModel = elasticSearchAccess.query(keyWord, fieldName, sorts, indexName, indexType, type, pageNo, pageSize);
        System.out.println("Got records：" + pageModel.getTotalRecords());
        printLocations(pageModel.getRecords());
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testQuery3() throws Exception {
        String keyWord = "未来城";
        Location location = elasticSearchAccess.query(keyWord, indexName, indexType, type);
        printLocation(location);
    }

    @Test
    // 没搞懂
    public void testQuery4() throws Exception {
        String keyWork = "天府";
        Map<String, Float> fieldsMap = new HashMap<String, Float>();
        fieldsMap.put("name", Float.valueOf("0.9"));
        // fieldsMap.put("age", Float.valueOf("0.1"));
        List<Sort> sorts = new ArrayList<Sort>();
        sorts.add(new Sort("age", Sorting.DESC));
        boolean isFuzzy = false;
        List<Location> location = elasticSearchAccess.query(keyWork, fieldsMap, sorts, indexName, indexType, type, isFuzzy);
        printLocations(location);
    }

    @Test
    public void testQuery5() throws Exception {
        String keyWork = "天府软件园";
        String[] fieldNames = { "name" };
        List<Location> locationList = elasticSearchAccess.query(keyWork, fieldNames, indexName, indexType, type);
        printLocations(locationList);
    }

    @Test
    public void testPrefixQuery() throws Exception {
        String keyWord = "中心";
        String fieldName = "name";
        List<Location> locationList = elasticSearchAccess.prefixQuery(keyWord, fieldName, indexName, indexType, type);
        printLocations(locationList);
    }

    @Test
    public void testQuery7() throws Exception {
        String keyWork = "中心";
        Map<String, Float> fieldsMap = new HashMap<String, Float>();
        fieldsMap.put("name", Float.valueOf("1"));
        List<Sort> sorts = new ArrayList<Sort>();
        sorts.add(new Sort("age", Sorting.DESC));
        boolean isFuzzy = false;
        PageModel<Location> pageModel = elasticSearchAccess.query(keyWork, fieldsMap, sorts, indexName, indexType, type, isFuzzy, pageNo, pageSize);
        if (null != pageModel.getRecords()) {
            printLocations(pageModel.getRecords());
        }
    }

    @Test
    // 没搞懂
    public void testDelete1() throws Exception {
        String keyWork = "天府";
        String[] fieldName = { "name" };
        boolean isFuzzy = true;
        elasticSearchAccess.delete(keyWork, fieldName, indexName, indexType, isFuzzy);
    }

    @Test
    // 删除指定ID的document.
    public void testDelete2() throws Exception {
        String indexId = "dxtHlt4USZeLfqPVFIeACw";
        elasticSearchAccess.delete(indexId, indexName, indexType);
    }

    @Test
    public void testBooleanQuery() throws Exception {
        String fieldName1 = "name";
        String fieldName2 = "age";
        String keyWord1 = "天府新区";
        String keyWord2 = "5";
        List<Location> list = elasticSearchAccess.booleanQuery(fieldName1, fieldName2, keyWord1, keyWord2, type, indexName, indexType);
        printLocations(list);
    }

    @Test
    public void testNestedBooleanQuery() throws Exception {
        String[] fieldNames = new String[] { "name", "age", "age" };
        String[] keyWords = new String[] { "金融中心", "4", "5" };
        List<Location> list = elasticSearchAccess.nestedBooleanQuery(fieldNames, keyWords, type);
        printLocations(list);
    }

    @Test
    public void testGetDocument() throws Exception {
        String indexId = "天府广场";
        Location location = elasticSearchAccess.getDocument(indexName, indexType, indexId, type);
        printLocation(location);
    }

    @Test
    public void testUpdateDocument() throws Exception {
        String indexId = "天府广场";
        Location location = elasticSearchAccess.getDocument(indexName, indexType, indexId, type);
        location.setAge(31);
        String data = JSONBinder.binder(Location.class).toJSON(location);
        elasticSearchAccess.updateDocument(indexName, indexType, indexId, data);
    }

    @Test
    public void testMultiGet() throws Exception {
        Doc doc1 = new Doc(indexName, indexType, "中信城");
        Doc doc2 = new Doc(indexName, indexType, "软件中心");
        Doc doc3 = new Doc(indexName, indexType, "天府中心");
        Doc doc4 = new Doc(indexName, indexType, "蜀都中心");
        List<Doc> docs = new ArrayList<Doc>();
        docs.add(doc1);
        docs.add(doc2);
        docs.add(doc3);
        docs.add(doc4);
        List<Location> locations = elasticSearchAccess.multiGet(docs, Location.class);
        printLocations(locations);
    }

    @Test
    public void testTermsQuery() throws Exception {
        String field = "name";
        String[] term = new String[] { "东方", "广场" };
        printLocations(elasticSearchAccess.termsQuery(field, term, indexName, indexType, type));
    }

    @Test
    public void testMoreLikeThisQuery() throws Exception {
        String[] fields = new String[] { "name" };
        printLocations(elasticSearchAccess.moreLikeThisQuery("天府新区天府软件园", fields, indexName, indexType, type));
    }

    @Test
    public void test() throws Exception {
        String keyWord = "东方 广场";
        String[] fieldNames = new String[] { "name" };
        PageModel<Location> pageModel = elasticSearchAccess.highPrecisionQuery(keyWord, fieldNames, indexName, indexType, type, pageNo, pageSize);
        printLocations(pageModel.getRecords());
    }

    @Test
    public void testCombineQuery() throws Exception {
        List<SearchCriterion> criteria = new ArrayList<SearchCriterion>();
        SearchCriterion searchCriterion = new SearchCriterion();
        searchCriterion.setField("name");
        searchCriterion.setKeyWord("天府");
        criteria.add(searchCriterion);

        List<RangeSearchCriterion> rangeCriteria = new ArrayList<RangeSearchCriterion>();
        RangeSearchCriterion rangeSearchCriterion = new RangeSearchCriterion();
        rangeSearchCriterion.setField("age");
        rangeSearchCriterion.setFrom(2);
        rangeSearchCriterion.setTo(4);
        rangeCriteria.add(rangeSearchCriterion);

        PageModel<Location> pageModel = elasticSearchAccess.combineQuery(criteria, rangeCriteria, indexName, indexType, type, pageNo, pageSize);
        printLocations(pageModel.getRecords());
    }

    private void printLocations(List<Location> locationList) {
        for (Location loc : locationList) {
            System.out.print("Name:" + loc.getName());
            System.out.println("  Age:" + loc.getAge());
        }
    }

    private void printLocation(Location location) {
        if (null != location) {
            System.out.print("Name:" + location.getName());
            System.out.println("  Age:" + location.getAge());
        } else {
            System.out.println("No record to print!");
        }
    }

}
