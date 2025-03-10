package com.hmall.item.es;

import cn.hutool.json.JSONUtil;
import com.hmall.item.domain.po.ItemDoc;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author ZC_Wu 汐
 * @date 2025/3/9 21:01
 * @description
 */
@SpringBootTest(properties = "spring.profiles.active=local")
public class ElasticSearchTest {
    private RestHighLevelClient client;

    /**
     * 快速入门
     * @throws IOException
     */
    @Test
    void testMatchAll() throws IOException {
        // 1. 创建request
        SearchRequest request = new SearchRequest("items"); // 搜索items库
        // 2. 配置request参数
        request.source()
                .query(QueryBuilders.matchAllQuery());
        // 3. 发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        System.out.println("response = " + response); // 原始的响应结果

        // 4. 解析结果
        SearchHits searchHits = response.getHits();
        parseResponseResult(searchHits);

    }

    /**
     * 复合查询 bool
     * 搜索关键字为脱脂牛奶,品牌必须为德亚,价格必须低于300
     * @throws IOException
     */
    @Test
    void testSearch() throws IOException {
        // 1. 创建request
        SearchRequest request = new SearchRequest("items"); // 搜索items库
        // 2. 组织DSL参数
        request.source()
                .query(QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery("name", "脱脂牛奶"))
                        .filter(QueryBuilders.termQuery("brand", "德亚"))
                        .filter(QueryBuilders.rangeQuery("price").lt(30000))
                );
        // 3. 发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        System.out.println("response = " + response); // 原始的响应结果

        // 4. 解析结果
        SearchHits searchHits = response.getHits();
        parseResponseResult(searchHits);

    }

    @Test
    void testSortAndPage() throws IOException {
        // 0. 模拟前端传递的分页参数
        int pageNo = 1, pageSize = 5;
        // 1. 创建request
        SearchRequest request = new SearchRequest("items"); // 搜索items库
        // 2. 组织DSL参数
        // 2.1 query 条件
        request.source().query(QueryBuilders.matchAllQuery());
        // 2.2 分页
        request.source().from((pageNo - 1) * pageSize).size(pageSize);
        // 2.3 排序
        request.source()
                .sort("sold", SortOrder.DESC)
                .sort("price", SortOrder.ASC); // 按销量倒序，如果销量一样按价格升序
        // 3. 发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        System.out.println("response = " + response); // 原始的响应结果

        // 4. 解析结果
        SearchHits searchHits = response.getHits();
        parseResponseResult(searchHits);
    }

    /**
     * 高亮查询
     * @throws IOException
     */
    @Test
    void testHighlight() throws IOException {
        // 1. 创建request
        SearchRequest request = new SearchRequest("items"); // 搜索items库
        // 2. 组织DSL参数
        // 2.1 query 条件
        request.source().query(QueryBuilders.matchQuery("name", " 脱脂牛奶"));
        // 2.2 高亮条件
        request.source().highlighter(
                SearchSourceBuilder.highlight()
                        .field("name")
                        .preTags("<em>")
                        .postTags("</em>")
        );

        // 3. 发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        System.out.println("response = " + response); // 原始的响应结果

        // 4. 解析结果
        parseResponseResult(response);
    }

    /**
     * 聚合查询
     * @throws IOException
     */
    @Test
    void testAgg() throws IOException {
        // 1. 创建request
        SearchRequest request = new SearchRequest("items"); // 搜索items库
        // 2. 组织DSL参数
        // 2.1 query 条件 查全部可以省略
        // request.source().query(QueryBuilders.matchAllQuery());
        // 2.2 分页
        request.source().size(0); // 设置`size`为0，就是每页查0条，则结果中就不包含文档，只包含聚合
        // 2.3 聚合条件
        String brandAggName = "brandAgg";
        request.source().aggregation(
                AggregationBuilders.terms(brandAggName) // 聚合的类型 terms分组查询  参数是聚合查询名
                        .field("brand").size(10) // 根据brand分组，查10个
        );

        // 3. 发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        System.out.println("response = " + response); // 原始的响应结果

        // 4. 解析结果
        Aggregations aggregations = response.getAggregations();
        // 4.1 根据聚合名称获取对应的聚合
        Terms brandTerms = aggregations.get(brandAggName);
        // 4.2 获取buckets桶(分组)
        List<? extends Terms.Bucket> buckets = brandTerms.getBuckets();
        // 4.3 遍历获取每一个bucket
        for (Terms.Bucket bucket : buckets) {
            System.out.println("brand = " + bucket.getKeyAsString());
            System.out.println("count = " + bucket.getDocCount());
        }


    }

    /**
     * 解析数据 2.0  可以解析高亮
     * @param response
     */
    private static void parseResponseResult(SearchResponse response) {
        SearchHits searchHits = response.getHits();
        // 4.1 总条数
        long total = searchHits.getTotalHits().value;
        System.out.println("total = " + total);
        // 4.2 命中的数据
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            // 4.2.1 获取source结果
            String json = hit.getSourceAsString();
            System.out.println("json = " + json);
            // 4.2.2 转为ItemDoc
            ItemDoc doc = JSONUtil.toBean(json, ItemDoc.class);
            // 4.3 处理高亮结果
            Map<String, HighlightField> hfs = hit.getHighlightFields(); // 获取高亮字段
            if (hfs != null && !hfs.isEmpty()) {
                // 4.3.1 根据高亮字段名获取结果
                HighlightField hf = hfs.get("name");
                // 4.3.2 获取高亮结果，覆盖非高亮结果
                String hfName = hf.getFragments()[0].string(); // 返回一个数据，这里只有一个直接用索引0即可，多个可以做字符串的拼接
                System.err.println(hfName);
                doc.setName(hfName); // 覆盖非高亮结果
            }
            System.out.println("doc = " + doc);
        }
    }


    /**
     * 解析数据 1.0 解析数据(不可解析高亮)
     * @param searchHits
     */
    private static void parseResponseResult(SearchHits searchHits) {
        // 4.1 总条数
        long total = searchHits.getTotalHits().value;
        System.out.println("total = " + total);
        // 4.2 命中的数据
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            // 4.2.1 获取source结果
            String json = hit.getSourceAsString();
            System.out.println("json = " + json);
            // 4.2.2 转为ItemDoc
            ItemDoc doc = JSONUtil.toBean(json, ItemDoc.class);
            System.out.println("doc = " + doc);
        }
    }

    @BeforeEach
    void setUp() {
        client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://192.168.244.130:9200")
        ));
    }
    @AfterEach
    void tearDown() throws Exception {
        if (client != null) {
            client.close();
        }
    }

}
