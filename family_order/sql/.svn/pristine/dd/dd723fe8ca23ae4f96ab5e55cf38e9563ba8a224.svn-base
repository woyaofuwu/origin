SELECT a.partition_id partition_id,to_char(a.user_id) user_id,a.rsrv_value_code rsrv_value_code,a.rsrv_value rsrv_value,a.rsrv_str1 rsrv_str1,a.rsrv_str2 rsrv_str2,a.rsrv_str3 rsrv_str3,a.rsrv_str4 rsrv_str4,a.rsrv_str5 rsrv_str5,a.rsrv_str6 rsrv_str6,a.rsrv_str7 rsrv_str7,c.channel_name||'['||a.rsrv_str1||']' rsrv_str8,a.rsrv_str9 rsrv_str9,a.rsrv_str10 rsrv_str10,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_user_other a,CHNL_channel c
 WHERE a.user_id = :USER_ID
   AND a.partition_id=:PARTITION_ID
   AND a.rsrv_value_code=:RSRV_VALUE_CODE
   AND c.channel_code = a.rsrv_str1
   AND sysdate BETWEEN a.start_date AND a.end_date