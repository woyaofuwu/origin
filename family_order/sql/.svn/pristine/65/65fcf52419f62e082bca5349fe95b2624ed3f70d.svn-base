--IS_CACHE=Y
SELECT service_id,state_code,state_name,priority,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM td_s_servicestate
 WHERE service_id=0
   AND sysdate BETWEEN start_date AND end_date