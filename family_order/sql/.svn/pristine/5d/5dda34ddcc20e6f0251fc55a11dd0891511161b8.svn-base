SELECT a.partition_id partition_id,to_char(b.user_id_b)  user_id,b.serial_number_b rsrv_str5,b.short_code rsrv_str6,a.rsrv_value_code rsrv_value_code,a.rsrv_value rsrv_value,a.rsrv_str1 rsrv_str1,nvl(a.rsrv_str2,'0') rsrv_str2,nvl(a.rsrv_str3,'0') rsrv_str3,a.rsrv_str4 rsrv_str4,a.rsrv_str7 rsrv_str7,a.rsrv_str8 rsrv_str8,a.rsrv_str9 rsrv_str9,a.rsrv_str10 rsrv_str10,to_char(b.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(b.end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_user_other a,tf_f_relation_uu b
 WHERE a.user_id(+)=b.user_id_b
   AND a.partition_id(+)=b.partition_id
   AND b.user_id_a=TO_NUMBER(:RSRV_STR5)
   AND b.relation_type_code='20'
   AND a.rsrv_value_code(+)=:RSRV_VALUE_CODE
   AND SYSDATE BETWEEN a.start_date(+) AND  a.end_date(+)
   AND SYSDATE BETWEEN b.start_date AND  b.end_date