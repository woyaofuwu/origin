SELECT COUNT(1) recordcount
  FROM tf_b_trade a
 WHERE a.trade_id = :TRADE_ID
   AND a.rsrv_str1 = (SELECT b.param_code
                        FROM td_s_commpara b
                       WHERE b.param_attr = '3005'
                         AND b.param_code = a.rsrv_str1
                         AND sysdate BETWEEN b.start_date AND b.end_date
                         AND (b.eparchy_code = :TRADE_EPARCHY_CODE OR b.eparchy_code='ZZZZ'))