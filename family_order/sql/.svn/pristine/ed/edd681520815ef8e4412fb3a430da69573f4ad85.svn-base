SELECT commemorate_type_code paracode,commemorate_type paraname
FROM td_s_commemoratetype
WHERE sysdate BETWEEN start_date AND end_date
   AND (:TRADE_EPARCHY_CODE is null or :TRADE_EPARCHY_CODE is not null)