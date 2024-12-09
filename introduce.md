黑马商城  2024最新SpringCloud微服务开发与实战，java黑马商城项目微服务实战开发（涵盖MybatisPlus、Docker、MQ、ES、Redis高级等）
[微服务01-02.认识微服务-单体架构_哔哩哔哩_bilibili](https://www.bilibili.com/video/BV1S142197x7?spm_id_from=333.788.player.switch&bvid=BV1S142197x7&vd_source=796ed40051b301bfa3a84ba357f4828c&p=37)
启动：
- centos7启动docker中的mysql
- hmall-nginx `start nginx.exe`

http://localhost:8080/doc.html

Centos:

```shell
ifup ens33
ip addr show
# root 目录下
docker compose up -d # 启动mysql等

docker start nacos # 这个

docker rm -f nacos

docker run -d \
--name nacos \
--env-file ./nacos/custom.env \
-p 8848:8848 \
-p 9848:9848 \
-p 9849:9849 \
--restart=always \
nacos/nacos-server:v2.1.0-slim # 启动nacos

docker network ls # 查看网络

docker inspect mysql # 查看mysql网络

docker network connect hmall nacos # 将nacos容器加入hmall网络中
# seata需要和mysql及nacos在同一个网络
docker run --name seata \
-p 8099:8099 \
-p 7099:7099 \
-e SEATA_IP=192.168.244.130 \
-v ./seata:/seata-server/resources \
--privileged=true \
--network hmall \
-d \
seataio/seata-server:1.5.2 # 启动seata

docker rm -f mq

docker run \
 -e RABBITMQ_DEFAULT_USER=itheima \
 -e RABBITMQ_DEFAULT_PASS=123456 \
 -v mq-plugins:/plugins \
 --name mq \
 --hostname mq \
 -p 15672:15672 \
 -p 5672:5672 \
 --network hmall\
 -d \
 rabbitmq:3.8-management # 启动mq
```

- **nacos**：http://192.168.244.130:8848/nacos
- **seata**：http://192.168.244.130:7099/#/login 
  admin admin
- **rabbitmq**：http://192.168.244.130:15672/ 
  itheima 123456

.jks 文件是 Java 密钥库（Java KeyStore）文件的扩展名。它用于存储密钥对和证书，这些密钥和证书通常用于 Java 应用程序的安全通信，例如 SSL/TLS 连接。以下是一些关于 .jks 文件的关键点：

1. **用途**：.jks 文件主要用于存储公钥和私钥，应用于身份验证和加密通信。它允许 Java 程序安全地管理和存储需要的密码材料。
2. **格式**：.jks 文件通常是二进制格式，使用 Java 内置的 KeyStore 类来访问和操作这些存储的密钥和证书。
3. **创建和管理**：可以使用 Java 的 `keytool` 命令行工具来创建和管理 .jks 文件。这个工具支持生成密钥对、导入证书以及导出密钥等操作。
