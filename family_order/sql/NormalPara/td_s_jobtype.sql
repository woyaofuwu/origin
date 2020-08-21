SELECT job_type_code paracode,job_type paraname FROM td_s_jobtype
 WHERE sysdate BETWEEN start_date AND end_date
   AND (:TRADE_EPARCHY_CODE IS NULL OR :TRADE_EPARCHY_CODE IS NOT NULL)