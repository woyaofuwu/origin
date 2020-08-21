--IS_CACHE=Y
SELECT service_id_a,service_id_b,
limit_tag,
to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,
to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code 
FROM TD_S_SERVICELIMIT 
WHERE (:SERVICE_ID_A = -1 OR SERVICE_ID_A = :SERVICE_ID_A)
ORDER BY service_id_a