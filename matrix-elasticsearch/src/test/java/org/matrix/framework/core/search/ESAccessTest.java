package org.matrix.framework.core.search;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.index.query.MatchQueryBuilder.Operator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.matrix.framework.core.collection.converter.JSONConverter;

public class ESAccessTest {

    private ElasticSearchSettings settings;
    private ESAccess esAccess;
    private Integer pageNo = 1;
    private Integer pageSize = 100;
    private String index = "matrix";
    private String type = "matrix_test";
    private JSONConverter jsonConverter = new JSONConverter();

    @Before
    public void before() {
        ESResultConverter converter = new ESResultConverter();
        converter.setJsonConverter(jsonConverter);

        String server = "192.168.2.119:9300";
        settings = new ElasticSearchSettings();
        settings.setOfficialServers(server.split(";"));
        settings.setClusterName("matrix_elasticsearch");
        // Server地址及端口
        settings.setDiscoveryFrequency(10L);
        esAccess = new ESAccess();
        esAccess.setEsResultConverter(converter);
        esAccess.setElasticSearchSettings(settings);
        esAccess.setJsonConverter(jsonConverter);
        esAccess.initialize();
    }

    @After
    public void after() {
        esAccess.shutdown();
    }

    @Test
    public void putDataTest() {
        Location location = new Location("周星驰", 1);
        IndexResponse response = esAccess.putData("matrix", type, jsonConverter.toString(location));
        System.out.println(response.getId());
        System.out.println(response.isCreated());
    }

    @Test
    public void putDataTest2() {
        Location location = new Location("潘凯", 26);
        IndexResponse response = esAccess.putData("matrix", type, "1", jsonConverter.toString(location));
        System.out.println(response.getVersion());
        System.out.println(response.isCreated());
    }

    @Test
    public void getDataTest() {
        Location locaiton = esAccess.getData(index, type, "1", Location.class);
        System.out.println(locaiton.getName());
        System.out.println(locaiton.getAge());
    }

    @Test
    public void updateDataTest() {
        Location location = new Location("潘凯", 27);
        esAccess.updateData(index, type, "1", jsonConverter.toString(location));
    }

    @Test
    public void deleteDataTest() {
        DeleteResponse response = esAccess.deleteData(index, type, "1");
        System.out.println(response.isFound());
    }

    @Test
    public void search() {
        List<String> indices = new ArrayList<String>();
        indices.add(index);
        List<String> types = new ArrayList<String>();
        types.add(type);
//        List<Location> result = esAccess.matchSearch(indices, types, null, "name", "周星", null, Operator.OR, null, pageNo, pageSize, Location.class);
//        for (Location location : result) {
//            System.out.println(location.getName());
//            System.out.println(location.getAge());
//        }

    }

}
