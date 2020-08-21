--IS_CACHE=Y
SELECT service_id,state_code_a,state_code_b,limit_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code 
  FROM td_s_svcstate_limit
 WHERE service_id=:SERVICE_ID
   AND state_code_a=:STATE_CODE_A
   AND limit_tag=:LIMIT_TAG
   AND sysdate BETWEEN start_date AND end_date
   AND (eparchy_code=:EPARCHY_CODE OR eparchy_code='ZZZZ')