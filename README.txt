����������TCP��������ʽ����Ҫ�ͻ��˲�����ͬ��ʽ

Json��ʽ��
uid		����id
targetId	����id
type		��������
dateTime	ʱ���������ʱ����и�ʽ����
content		�ı�����/�ļ�·������С���Զ��Ÿ���

����uid��targetId��
��server����ʱuidΪ����id��targetIdΪĿ��id
����ʱuidΪ����id��targetIdΪ������Դ
server�ڶ�message����ת��ʱ��Ҫ����uid��targetId��ת�������������ݿ�ʱ����Ҫ

Message��
���������¼�������ݿ�

Mysql��
��Ҫ����SET SQL_SAFE_UPDATES = 0	�ر�safe-updatesģʽ����sql������뼴��

friends:
���ʱ���������¼��ɾ��ʱͬʱɾ�����޸�ʱ���޸Ķ�Ӧһ����¼

�ļ��շ���
contentΪ(�ļ���,size)
�洢·����δ����
typeΪSEND_FILEʱ��server��Ҫ�������Կͻ��˵��ļ����ݴ浽����
typeΪRECV_FILEʱ��server��Ҫ���ļ����͵���Ӧ�Ŀͻ���

onlineList��
������������login��logout������������⹲ͬά������ʱû���õ�����Ϊû������Ƿ����ߵ����ԣ�

UnsendMessage��
key���д�������Ϣ��uid
value��ArrayList<Message>
����Ӧuid��server�������Ӻ�server��ArrayList�е���Ϣһ���Է����������ArraryList

