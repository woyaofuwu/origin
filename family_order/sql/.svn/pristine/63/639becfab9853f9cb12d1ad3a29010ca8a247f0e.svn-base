select t.rsrv_str1,
       t.rsrv_tag1,
       t.rsrv_str2,
       t.rsrv_tag2,
       t.extend_value
from tf_f_cust_group_extend t
where 1 = 1
and   t.extend_tag = 'BNUMB'
and   t.extend_value = :CUST_ID
and   t.rsrv_str2 = :BNUM_KIND_NEW
and   t.rsrv_tag1 = '1'