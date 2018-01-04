服务器采用TCP长连接形式，需要客户端采用相同形式

Json格式：
uid		自身id
targetId	对象id
type		请求类型
dateTime	时间戳（传输时请进行格式化）
content		文本内容/文件路径及大小，以逗号隔开

关于uid和targetId：
向server发送时uid为自身id，targetId为目标id
接收时uid为自身id，targetId为发送来源
server在对message进行转发时需要进行uid和targetId的转换，但存入数据库时不需要

Message：
仅将聊天记录存入数据库

Mysql：
需要设置SET SQL_SAFE_UPDATES = 0	关闭safe-updates模式，以sql语句输入即可

friends:
添加时添加两条记录，删除时同时删除，修改时仅修改对应一条记录

文件收发：
content为(文件名,size)
存储路径尚未定义
type为SEND_FILE时，server需要接收来自客户端的文件并暂存到本地
type为RECV_FILE时，server需要将文件发送到对应的客户端

onlineList：
在线名单，由login，logout操作及心跳检测共同维护（暂时没有用到，因为没有添加是否在线的属性）

UnsendMessage：
key：有待接收消息的uid
value：ArrayList<Message>
当对应uid与server建立连接后，server将ArrayList中的消息一次性发出，并清空ArraryList

