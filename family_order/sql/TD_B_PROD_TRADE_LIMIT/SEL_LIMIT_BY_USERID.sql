SELECT pid,id_type,trade_type_code,months,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,eparchy_code,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM td_b_prod_trade_limit a
 WHERE trade_type_code =:TRADE_TYPE_CODE AND id_type='0' AND pid IN 
                      (SELECT product_id FROM tf_f_user_infochange 
                        WHERE user_id=:USER_ID AND SYSDATE BETWEEN start_date AND end_date
                          AND (a.months = 0 OR SYSDATE<=add_months(start_date,a.months)))
   AND (eparchy_code=:EPARCHY_CODE or eparchy_code = 'ZZZZ')  AND sysdate BETWEEN start_date AND end_date
UNION ALL
SELECT pid,id_type,trade_type_code,months,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,eparchy_code,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM td_b_prod_trade_limit a
 WHERE trade_type_code =:TRADE_TYPE_CODE AND id_type='1' AND pid IN 
                      (SELECT service_id FROM tf_f_user_svc
                        WHERE user_id=:USER_ID AND SYSDATE BETWEEN start_date AND end_date
                          AND (a.months = 0 OR SYSDATE<=add_months(start_date,a.months)))
   AND (eparchy_code=:EPARCHY_CODE or eparchy_code = 'ZZZZ')  AND sysdate BETWEEN start_date AND end_date
UNION ALL
SELECT pid,id_type,trade_type_code,months,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,eparchy_code,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM td_b_prod_trade_limit a
 WHERE trade_type_code =:TRADE_TYPE_CODE AND id_type= '2' AND pid IN
                       (SELECT discnt_code FROM tf_f_user_discnt 
                         WHERE user_id=:USER_ID AND SYSDATE BETWEEN start_date AND end_date
                           AND (a.months = 0 OR SYSDATE <= add_months(start_date,a.months)))
   AND (eparchy_code=:EPARCHY_CODE or eparchy_code = 'ZZZZ')  AND sysdate BETWEEN start_date AND end_date