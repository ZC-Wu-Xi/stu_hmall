package com.hmall.item.es;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author ZC_Wu 汐
 * @date 2025/1/6 17:50
 * @description ElasticSearch 操作索引库(表)
 */
public class ElasticIndexTest {
    private RestHighLevelClient client;

    @Test
    void testConnection() {
        System.out.println("client = " + client);
    }

    /**
     * 创建索引库
     * @throws IOException
     */
    @Test
    void testCreateIndex() throws IOException {
        // 1.创建Request对象
        CreateIndexRequest request = new CreateIndexRequest("items"); // PUT /items
        // 2.准备请求参数
        request.source(MAPPING_TEMPLATE, XContentType.JSON); // json参数 {...}
        // 3.发送请求
        client.indices().create(request, RequestOptions.DEFAULT); // create创建索引库
    }

    /**
     * 查询索引库
     * @throws IOException
     */
    @Test
    void testGetIndex() throws IOException {
        // 1.创建Request对象
        GetIndexRequest request = new GetIndexRequest("items");// GET /items
        // 2. 发送请求
        GetIndexResponse getIndexResponse = client.indices().get(request, RequestOptions.DEFAULT);// 查询索引库，返回详细信息
        System.out.println("getIndexResponse = " + getIndexResponse);
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);// 查询索引库是否存在
        System.out.println("exists = " + exists);
    }

    /**
     * 删除索引库
     * @throws IOException
     */
    @Test
    void testDeleteIndex() throws IOException {
        // 1.创建Request对象
        DeleteIndexRequest request = new DeleteIndexRequest("items"); // DELETE /items
        // 2.发送请求
        client.indices().delete(request, RequestOptions.DEFAULT); // delete 删除索引库
    }



    // json字符串
    static final String MAPPING_TEMPLATE = "{\n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"name\":{\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"ik_max_word\"\n" +
            "      },\n" +
            "      \"price\":{\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"stock\":{\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"image\":{\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"category\":{\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"brand\":{\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"sold\":{\n" +
            "        \"type\": \"integer\"\n" +
            "      },\n" +
            "      \"commentCount\":{\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"isAD\":{\n" +
            "        \"type\": \"boolean\"\n" +
            "      },\n" +
            "      \"updateTime\":{\n" +
            "        \"type\": \"date\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    @BeforeEach
    void setUp() {
        // 初始化客户端
        client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://192.168.244.130:9200")
        ));
    }

    @AfterEach
    void tearDown() throws IOException {
        // 释放资源
        if (client != null) {
            client.close();
        }
    }
}
