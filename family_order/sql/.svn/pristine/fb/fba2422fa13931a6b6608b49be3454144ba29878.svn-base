--IS_CACHE=Y
SELECT staff_id,right_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
  FROM td_m_tempfuncright
 WHERE staff_id=:STAFF_ID
   AND (:RIGHT_CODE is null or right_code=:RIGHT_CODE)
   AND (end_date is null or end_date>=sysdate)