--IS_CACHE=Y
SELECT discnt_code,service_id,limit_tag 
  FROM td_s_discnt_svc_limit
 WHERE service_id=:SERVICE_ID
   AND sysdate BETWEEN start_date AND end_date
   AND (eparchy_code=:EPARCHY_CODE OR eparchy_code='ZZZZ')