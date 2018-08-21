## 项目简介

本项目是针对 intoyun 第三方服务接入服务器端开源实现, 采用 clojure 编程语言
开发， 充分利用函数式编程的优势， 提高开发效率， 结合丰富的java 库， 可以快速实
现自己的后台服务器！

项目主要包括三部分：

1. 基于 ring 库开发的 web 服务框架， 用户可以直接添加路由以及对应的handler来实现
   http业务！

2. 集成 kafka 消费者， 接收 intoyun 平台推送过来的设备实时数据

3. 集成 mysql 数据库接口， 方便用户操作 mysql 数据库

4. 集成 websocket 长连接推送

## TODO

1. 统一配置文件
2. http 代理

## 快速开始

### 安装 lein

参考官方文档  https://github.com/technomancy/leiningen

1. macos 

```
brew install leiningen
```

2. ubunut

2.1 下载安装脚本

```
cd /usr/local/bin
wget https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein
chmod 755 lein
```

2.2 强烈建议 terminal fq(shadowsocks+Polipo) + 导出环境变量

```
export HTTP_CLIENT="wget --no-check-certificate -O"
```

2.3 安装 leiningen

```
lein
```

### 初始化 mysql

项目假设用户使用 mysql 作为数据持久化解决方案 , 需要在本地启动 mysql, 这里采用
docker部署 mysql, 并手动创建数据库:mydb 以及用户:molmc 密码: 123456

1. 启动 mysql (以下脚本会帮你完成mysql 启动）

```
cd /path/to/intoyun-enterprise-demo-clojure
cd resources
./startdeps.sh
```

2. 进入docker
```
docker exec -it mysql sh
```

3. 创建数据库 用户 密码

```
  mysql -u root -p
       输入 123456

  GRANT ALL PRIVILEGES ON *.* TO molmc@'%' IDENTIFIED BY '123456' WITH GRANT OPTION;
  CREATE DATABASE mydb;
  FLUSH PRIVILEGES;
```

4. 退出 mysql docker containner

5. 回到项目根目录， 创建数据库表,可以修改 (migrations/20180816102714302-accounts.clj)

```
cd /path/to/intoyun-enterprise-demo-clojure
vi project.clj (修改 mysql 的配置项, 可以使用默认值, 和下面配置mysql一致) 
lein clj-sql-up migrate

```

### 修改配置文件(todo: 稍后会统一配置)

```
cd /path/to/intoyun-enterprise-demo-clojure
vi src/jdbc/korma.clj  修改 mysql 配置 (可以使用默认的值)
vi  src/kafka/consumer.clj 修改 20, 21 两行 username， password （改为服务器授权的 appid， appsecret）
```

### 启动服务
1. 第一次启动之前请先拉取依赖， 可能需要半个小时左右， 视网络情况

  lein deps

2. 启动服务(http: 8080, websocket: 9090)

  lein run 8080

### Webserver 接口实现

```
curl -d '{"uname":"alice", "password":"123456"}' -H "Content-Type: application/json" -X POST http://localhost:8080/accounts/register
```

### Kafka 实时数据推送现实

设备接入intoyun之后， 设备的实时数据就会由 intoyun 推送到本服务， 可以看到推送消息！
可以自行修改 src/kfk/parser.clj 文件来实现自己的服务器端业务逻辑。


## 注意： 最好将 terminal fq， 建议 shadowsocks+Polipo, 程序员都懂得！
