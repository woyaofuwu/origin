SELECT serial_number,idtype,state,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,rsrv_str1,rsrv_str2,rsrv_str3,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM tf_f_blacksms
 WHERE (:SERIAL_NUMBER IS NULL OR (:SERIAL_NUMBER IS NOT NULL AND serial_number=:SERIAL_NUMBER))
   AND state=:STATE
   AND (:START_DATE IS NULL OR (:START_DATE IS NOT NULL 
                                 AND start_date>=TO_DATE(:START_DATE, 'YYYY-MM-DD')
                                 AND start_date<TO_DATE(:START_DATE, 'YYYY-MM-DD')+1))
   AND (:RSRV_STR3 IS NULL OR (:RSRV_STR3 IS NOT NULL AND rsrv_str3=:RSRV_STR3))