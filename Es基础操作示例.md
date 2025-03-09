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

