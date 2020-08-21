--IS_CACHE=Y
SELECT service_id, res_type_code
  FROM td_s_service_res
 WHERE service_id = :SERVICE_ID
   AND sysdate BETWEEN start_date AND end_date