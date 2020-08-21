--IS_CACHE=Y
SELECT staff_id,data_code,data_type,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
  FROM td_m_tempdataright
 WHERE staff_id=:STAFF_ID
   AND (:DATA_CODE is null or data_code=:DATA_CODE)
   AND (:DATA_TYPE is null or data_type=:DATA_TYPE)
   AND (end_date is null or end_date>=sysdate)