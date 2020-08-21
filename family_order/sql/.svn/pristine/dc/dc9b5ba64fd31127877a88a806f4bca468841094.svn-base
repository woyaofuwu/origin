SELECT /*+index(a,IDX_TF_F_USER_OTHER_DATE)*/ partition_id,to_char(user_id) user_id,rsrv_value_code,rsrv_value,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_user_other a
 WHERE rsrv_value_code='GGTH'
  AND start_date>=TRUNC(TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'))
  AND start_date<=TRUNC(TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'))
  AND end_date>sysdate
  AND (:RSRV_STR1 is null or a.rsrv_str1=:RSRV_STR1)
  AND (:RSRV_STR2 is null or a.rsrv_str2=:RSRV_STR2)
  AND (:RSRV_STR3 is null or a.rsrv_str3=:RSRV_STR3)
  AND (:RSRV_STR4 is null or a.rsrv_str4=:RSRV_STR4)
  AND (:RSRV_STR5 is null or a.rsrv_str5=:RSRV_STR5)
  AND (:RSRV_STR6 is null or a.rsrv_str6=:RSRV_STR6)
  AND (:RSRV_STR7 is null or a.rsrv_str7=:RSRV_STR7)
  AND (:RSRV_STR8 is null or a.rsrv_str8=:RSRV_STR8)
  AND (:RSRV_STR9 is null or a.rsrv_str9=:RSRV_STR9)
  AND (:SERIAL_NUMBER is null or rsrv_str10=:SERIAL_NUMBER)
  AND (:CARD_NO is null or rsrv_value=:CARD_NO)