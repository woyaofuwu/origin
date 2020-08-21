select t.rsrv_str1,
       t.rsrv_tag1,
       decode(rsrv_str2,'1','01','2','02','3','03',rsrv_str2) rsrv_str2,
       t.rsrv_tag2,
       t.extend_value
from tf_f_cust_group_extend t
where 1 = 1
and   t.extend_tag = :EXTEND_TAG
and   t.extend_value = :CUST_ID
and   decode(rsrv_str2,'1','01','2','02','3','03',rsrv_str2) = :BNUM_KIND_NEW
and   t.rsrv_tag1 = '1'