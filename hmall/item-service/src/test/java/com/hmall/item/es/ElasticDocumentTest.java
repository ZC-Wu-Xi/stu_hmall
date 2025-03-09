package com.hmall.item.es;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmall.item.domain.po.Item;
import com.hmall.item.domain.po.ItemDoc;
import com.hmall.item.service.IItemService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

/**
 * @author ZC_Wu 汐
 * @date 2025/1/6 19:04
 * @description ElasticSearch 操作文档(数据)
 */
@SpringBootTest(properties = "spring.profiles.active=local")
public class ElasticDocumentTest {
    private RestHighLevelClient client;

    @Autowired
    private IItemService itemService;

    /**
     * 添加(全量修改)文档(数据)
     * @throws IOException
     */
    @Test
    void testIndexDocument() throws IOException {
        // 0. 准备文档数据
        // 0.1 根据id查询数据库中数据
        Item item = itemService.getById(546872L);
        // 0.2 把数据库数据转为文档数据
        ItemDoc itemDoc = BeanUtil.copyProperties(item, ItemDoc.class);

        // 1.创建request对象
        IndexRequest request = new IndexRequest("items").id(itemDoc.getId());
//        IndexRequest request = new IndexRequest("items").id("12345");
        // 2.准备JSON文档
//        request.source("{  \n" +
//                "  \"name\": \"Jack\",  \n" +
//                "  \"age\": 21,  \n" +
//                "  \"id\": 20  \n" +
//                "}", XContentType.JSON);
        request.source(JSONUtil.toJsonStr(itemDoc), XContentType.JSON);

        // 3.发送请求
        client.index(request, RequestOptions.DEFAULT);
    }

    /**
     * 查询文档(数据)
     * @throws IOException
     */
    @Test
    void testGetDocumentById() throws IOException {
        // 1.准备Request对象
//        GetRequest request = new GetRequest("items").id("100002644680");
        GetRequest request = new GetRequest("items").id("317578");
        // 2.发送请求
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        // 3.解析结果，获取响应结果中的source(我们需要的结果)
        String json = response.getSourceAsString();

        ItemDoc itemDoc = JSONUtil.toBean(json, ItemDoc.class);
        System.out.println("itemDoc= " + itemDoc);
    }

    /**
     * 删除文档(数据)
     * @throws IOException
     */
    @Test
    void testDeleteDocument() throws IOException {
        // 1.准备Request，两个参数，第一个是索引库名，第二个是文档id
//        DeleteRequest request = new DeleteRequest("items", "100002644680"); // DELETE /item/_doc/100002644680
        DeleteRequest request = new DeleteRequest("items", "12345"); // DELETE /item/_doc/12345
        // 2.发送请求
        client.delete(request, RequestOptions.DEFAULT);
    }

    /**
     * 修改(局部修改)文档(数据)
     * @throws IOException
     */
    @Test
    void testUpdateDocument() throws IOException {
        // 1.准备Request
//        UpdateRequest request = new UpdateRequest("items", "100002644680");
        UpdateRequest request = new UpdateRequest("items", "12345");
        // 2.准备请求参数
        request.doc(
                "id", 28,
                "price", 58800,
                "commentCount", 1
        );
        // 3.发送请求
        client.update(request, RequestOptions.DEFAULT);
    }

    /**
     * 批处理
     * 将数据库中商品数据存入items索引库
     * @throws IOException
     */
    @Test
    void testBulkDoc() throws IOException {
        // 分页查询商品数据
        int pageNo = 1;
        int size = 500;
        while (true) {
            // 1. 准备文档数据
            Page<Item> page = itemService.lambdaQuery()
                    .eq(Item::getStatus, 1)
                    .page(Page.of(pageNo, size));
            List<Item> records = page.getRecords();
            if (records == null || records.isEmpty()) {
                // 结束循环
                return;
            }
            // 2. 创建Request
            BulkRequest request = new BulkRequest();
            // 3. 准备请求参数
            for (Item item : records) {
                request.add(new IndexRequest("items")
                        .id(item.getId().toString())
                        .source(JSONUtil.toJsonStr(BeanUtil.copyProperties(item, ItemDoc.class)), XContentType.JSON)
                );
            }
            // 4. 发送请求
            client.bulk(request, RequestOptions.DEFAULT);
            // 5. 翻页
            pageNo++;
        }
    }

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
