SELECT PARAM_CODE paracode, PARAM_NAME paraname FROM (
SELECT DISTINCT PARAM_CODE ,PARAM_NAME  FROM td_b_purchaserule WHERE sysdate BETWEEN start_date and end_date AND (eparchy_code = :TRADE_EPARCHY_CODE OR eparchy_code = 'ZZZZ')
GROUP BY PARAM_CODE,PARAM_NAME)