SELECT partition_id,to_char(user_id) user_id,rsrv_value_code,rsrv_value,rsrv_str1,rsrv_str2,
decode(rsrv_str3,'0','全球通积分','1','动感地带积分') rsrv_str3, rsrv_str4,
rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,decode(rsrv_str9,'1','是','','否') rsrv_str9,rsrv_str10,
to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_user_other
 WHERE rsrv_value_code='KQFW'
  and  user_id=:USER_ID
  AND sysdate BETWEEN start_date+0 AND end_date+0