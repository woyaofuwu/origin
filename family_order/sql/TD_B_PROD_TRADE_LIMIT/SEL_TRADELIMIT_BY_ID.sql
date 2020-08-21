--IS_CACHE=Y
SELECT pid,id_type,trade_type_code,months,to_char(start_date, 'yyyy-mm-dd hh24:mi:ss'))start_date,to_char(end_date, 'yyyy-mm-dd hh24:mi:ss'))end_date,eparchy_code, to_char(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM td_b_prod_trade_limit
 WHERE pid=:PID
   AND id_type=:ID_TYPE
   AND (eparchy_code=:EPARCHY_CODE OR eparchy_code='ZZZZ')
   AND SYSDATE BETWEEN start_date AND end_date