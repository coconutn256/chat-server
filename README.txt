Json格式：
uid		自身id
targetId	对象id
type		请求类型
dateTime	时间戳（传输时请进行格式化）
content		文本内容

Mysql：
SET SQL_SAFE_UPDATES = 0	关闭safe-updates模式

friends:
添加时添加两条记录，删除时同时删除，修改时仅修改对应一条记录

请求处理：
Log in:成功，返回jsonArray:第一条为"type":LOGIN_SUCESS,之后为朋友信息friend_id,remark,tag;失败，返回jsonArry，第一条为"type":LOGIN_Fail

