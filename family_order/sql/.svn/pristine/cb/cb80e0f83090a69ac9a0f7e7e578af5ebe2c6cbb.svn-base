SELECT a.partition_id,to_char(a.user_id) user_id,a.rsrv_value_code,a.rsrv_value,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,a.rsrv_str6,a.rsrv_str7,a.rsrv_str8,a.rsrv_str9,b.serial_number rsrv_str10,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_user_other a,tf_f_user b
 WHERE a.rsrv_value_code=:RSRV_VALUE_CODE
   AND b.city_code=:CITY_CODE
   AND a.partition_id=MOD(TO_NUMBER(a.user_id),10000)
   AND a.user_id=b.user_id
   AND a.partition_id=b.partition_id
   AND (:SERIAL_NUMBER is null or b.serial_number=:SERIAL_NUMBER)
   AND (:RSRV_VALUE is null or a.rsrv_value=:RSRV_VALUE)
   AND a.start_date>=TRUNC(TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'))
   AND a.start_date<TRUNC(TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS'))+1
   AND Rownum<1000