### 分词器

```json
# 标准分词器
POST /_analyze
{
  "analyzer": "standard",
  "text": "黑马程序员学习java太棒了"
}

# 插件 ik分词器——智能语义切分 
POST /_analyze
{
  "analyzer": "ik_smart",
  "text": "传智播客开设大学,真的泰裤辣阿！"
}

# 插件 ik分词器——最细粒度切分
POST /_analyze
{
  "analyzer": "ik_max_word",
  "text": "传智播客开设大学,真的泰裤辣阿！"
}
```

### 索引库(表)操作

```json
# 创建索引库并设置mapping映射
PUT /heima
{
  "mappings": {
    "properties": {
      "info":{
        "type": "text",
        "analyzer": "ik_smart"
      },
      "email": {
        "type": "keyword",
        "index": false
      },
      "name": {
        "type": "object",
        "properties": {
          "firstName": {
            "type": "keyword"
          },
          "lastName": {
            "type": "keyword"
          }
        }
      }
    }
    
  }
}

# 查询索引库
GET /heima

# 删除索引库
DELETE /heima

# 修改索引库(添加新字段)
PUT /heima/_mapping
{
  "properties": {
    "age":{
      "type": "integer"
    }
  }
}
```

### 文档(数据)操作

```json
# 新增文档
POST /heima/_doc/1
{
  "info": "黑马程序员Java讲师",
  "email": "zy@itcast.cn",
  "name": {
    "firstName": "云",
    "lastName": "赵"
  }
}

# 查询文档
GET /heima/_doc/1

# 删除文档
DELETE /heima/_doc/1

# 全量修改(覆盖修改)
PUT /heima/_doc/1
{
    "info": "黑马程序员高级Java讲师",
    "email": "z1y@itcast.cn",
    "name": {
        "firstName": "云",
        "lastName": "赵"
    }
}

# 局部修改
POST /heima/_update/1
{
  "doc": {
    "email": "ZhaoYun@itcast.cn"
  }
}

# 批量新增
POST /_bulk
{"index": {"_index":"heima", "_id": "3"}}
{"info": "黑马程序员C++讲师", "email": "ww@itcast.cn", "name":{"firstName": "五", "lastName":"王"}}
{"index": {"_index":"heima", "_id": "4"}}
{"info": "黑马程序员前端讲师", "email": "zhangsan@itcast.cn", "name":{"firstName": "三", "lastName":"张"}}

# 批量删除
POST /_bulk
{"delete":{"_index":"heima", "_id": "3"}}
{"delete":{"_index":"heima", "_id": "4"}}
```

### DSL叶子查询

```json
# DSL查询
# 查询所有
GET /items/_search
{
  "query": {
    "match_all": {}
  }
}

# match所有 对一个字段检索
GET /items/_search
{
  "query": {
    "match": {
      "name": "脱脂牛奶"
    }
  }
}

# mult_match所有 同时对多个字段检索
GET /items/_search
{
  "query": {
    "multi_match": {
      "query": "华为"
      , "fields": ["name", "brand"]
    }
  }
}

# term所有 精确查询 # 脱脂、牛奶是两个词，因为term不分词，所以搜不到
GET /items/_search
{
  "query": {
    "term": {
      "name": {
        "value": "脱脂牛奶"
      }
    }
  }
}

# term所有 精确查询 # 德亚，一个词，可以搜到
GET /items/_search
{
  "query": {
    "term": {
      "brand": {
        "value": "德亚"
      }
    }
  }
}

# range所有 精确查询 范围查询
GET /items/_search
{
  "query": {
    "range": {
      "price": {
        "gte": 500000,
        "lte": 1000000
      }
    }
  }
}

# ids所有 根据文档id批量查询
GET /items/_search
{
  "query": {
    "ids": {
      "values": ["613360", "613359"]
    }
  }
}
```

### DSL复合查询

```json
# bool布尔查询 # 我们要搜索"智能手机"，但品牌必须是华为，价格必须是900~1599
GET /items/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "name": "智能手机"
          }
        }
      ],
      "filter": [
        {
          "term": {
            "brand": "华为"
          }
        },
        {
          "range": {
            "price": {
              "gte": 90000,
              "lte": 159900
            }
          }
        }
      ]
    }
  }
}


# 复合查询 结果排序 # 搜索商品，按照销量排序，销量一样则按照价格升序
GET /items/_search
{
  "query": {
    "match_all": {}
  },
  "sort": [
    {
      "sold": "desc"
    },
    {
      "price": "asc"
    }
  ]
}
# 复合查询 结果分页排序 # 搜索商品，查询出销量排名前10的商品，销量一样时按照价格升序
GET /items/_search
{
  "query": {
    "match_all": {}
  },
  "from": 0,
  "size": 10, 
  "sort": [
    {
      "sold": "desc"
    },
    {
      "price": "asc"
    }
  ]
}

# 高亮测试
GET /items/_search
{
  "query": {
    "match": {
      "name": "脱脂牛奶"
    }
  },
  "highlight": {
    "fields": {
      "name": {
        "pre_tags": "<em>",
        "post_tags": "</em>"
      }
    }
  }
}
```

### DSL聚合查询

```json
# terms类型分组 根据category字段值进行分组
# select category, count(*) from items group by category
GET /items/_search
{
  "size": 0,
  "aggs": {
    "cate_agg": {
      "terms": {
        "field": "category",
        "size": 5
      }
    },
    "brand_agg": {
      "terms": {
        "field": "brand",
        "size": 5
      }
    }
  }
}


# term分组 结合查询条件进行带条件的聚合 不同品牌大于3000元的手机
GET /items/_search
{
  "query": {
    "bool": {
      "filter": [
        {
          "term": {
            "category": "手机"
          }
        },
        {
          "range": {
            "price": {
              "gte": 300000
            }
          }
        }
      ]
    }
  }, 
  "size": 0,
  "aggs": {
    "brand_agg": {
      "terms": {
        "field": "brand",
        "size": 5
      }
    }
  }
}


# term分组 带条件的聚合 按品牌分组，并计算各个品牌价的最大最小平均值
GET /items/_search
{
  "query": {
    "bool": {
      "filter": [
        {
          "term": {
            "category": "手机"
          }
        }
      ]
    }
  }, 
  "size": 0,
  "aggs": {
    "brand_agg": {
      "terms": {
        "field": "brand",
        "size": 5
      },
      "aggs": {
        "price_stats": {
          "stats": {
            "field": "price"
          }
        }
      }
    }
  }
}
```

