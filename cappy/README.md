# 用于两个RabbitMQ之间的消息传递的中间件

## 作用
自来源端的Queue中取出数据并且可信的发如远端的RabbitMQ中，然后ACK，中间过程保证信息不丢失

## 启动方式
### 命令行方式（此项目优先）
参数列表：
-p 来源与目标的对应关系，首先写来源的URI，然后,间隔Queue名，然后|间隔目标URI，用逗号间隔依次写Exchange名和RoutingKey。用分号间隔每个分组形如：amqp://guest:123@192.168.0.1:4567/vhost,queue|amqp://admin:123@192.168.0.2:4567/vhost,exchange,key"

### 环境变量方式
暂时尚未支持