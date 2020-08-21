SELECT svcrequest_code paracode,svcrequest_name paraname FROM TD_S_SVCREQUEST_TYPE
 WHERE sysdate BETWEEN start_date AND end_date
   AND (:TRADE_EPARCHY_CODE IS NULL OR :TRADE_EPARCHY_CODE IS NOT NULL)