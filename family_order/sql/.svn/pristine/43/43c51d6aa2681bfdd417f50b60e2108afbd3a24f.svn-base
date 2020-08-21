select t.rsrv_str1,
       t.rsrv_str2,
       t.rsrv_str3,
       t.rsrv_str4,
       t.rsrv_str5,
       t.rsrv_str6,
       t.rsrv_str7,
       t.extend_tag,
       t.extend_value
from tf_f_cust_group_extend t
 where t.extend_value=:EXTEND_VALUE
  and t.extend_tag=:EXTEND_TAG
  and t.rsrv_str1=:RSRV_STR1
  and t.rsrv_str2=:RSRV_STR2
  and sysdate between t.rsrv_date1 and t.rsrv_date2