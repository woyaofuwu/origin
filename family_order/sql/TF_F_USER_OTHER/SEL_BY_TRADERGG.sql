SELECT partition_id,to_char(user_id) user_id,rsrv_value_code,rsrv_value,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_user_other
 WHERE rsrv_value_code=:RSRV_VALUE_CODE
   AND (:SERIAL_NUM is null or rsrv_str10 = :SERIAL_NUM)
   AND (:GG_CARDNO is null or rsrv_value = :GG_CARDNO)
   AND (:CASH_AWARDTYPE is null or rsrv_str2 = :CASH_AWARDTYPE)
   AND (:AWARD_GRADE is null or rsrv_str3 = :AWARD_GRADE)
   AND (:ATTACH_REASON is null or rsrv_str1 = :ATTACH_REASON)
   AND start_date>=TRUNC(TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'))
   AND start_date<TRUNC(TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'))+1
   AND rownum< 100