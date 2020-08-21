select t.rsrv_str1,
       t.rsrv_tag1,
       t.rsrv_str2,
       t.rsrv_str3,
       t.rsrv_tag2,
       t.extend_value 
from tf_f_cust_group_extend t,tf_f_cust_group g
where 1 = 1
and   t.extend_tag = 'vpntag'
and   t.extend_value = g.cust_id
and   g.remove_tag = '0'
and   t.rsrv_tag1 = :RSRV_TAG1
and   t.rsrv_tag2 = :RSRV_TAG2
and   g.group_id = :GROUP_ID