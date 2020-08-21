--IS_CACHE=Y
SELECT exchange_type_code,exchange_type,exchange_type_limit,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM td_b_score_exchange_type
 WHERE (eparchy_code=:EPARCHY_CODE OR eparchy_code ='ZZZZ')
    AND sysdate between start_date and end_date