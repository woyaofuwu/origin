--IS_CACHE=Y
SELECT discnt_code,service_id,
limit_tag,
to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,
to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code  
FROM TD_S_DISCNT_SVC_LIMIT 
WHERE (:DISCNT_CODE = -1 OR discnt_code = :DISCNT_CODE)
ORDER BY discnt_code