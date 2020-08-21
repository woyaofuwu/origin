SELECT  TO_CHAR(a.trade_id) trade_id, TO_CHAR(a.accept_date, 'yyyy-mm-dd hh24:mi:ss') accept_date, a.trade_type_code, b.trade_type
 FROM   (SELECT a.trade_id, a.trade_type_code, a.trade_eparchy_code, a.accept_date
         FROM   tf_bh_trade a, td_s_tradetype_limit b
         WHERE  a.serial_number = :SERIAL_NUMBER
         AND    a.cancel_tag = '0'
         AND    a.accept_date+0 > TO_DATE(:ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss')
         AND    b.limit_trade_type_code = a.trade_type_code
         AND    b.trade_type_code = :TRADE_TYPE_CODE
         AND    b.eparchy_code = a.trade_eparchy_code
         AND    b.limit_attr = '1'
         AND    b.limit_tag = '0'
         AND    SYSDATE BETWEEN b.start_date AND b.end_date
         AND    ROWNUM <= 1
         UNION ALL
         SELECT   a.trade_id, a.trade_type_code, a.trade_eparchy_code, a.accept_date
         FROM   tf_b_trade a, td_s_tradetype_limit b
         WHERE  a.serial_number = :SERIAL_NUMBER
         AND    a.cancel_tag = '0'
         AND    a.accept_date+0 > TO_DATE(:ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss')
         AND    b.limit_trade_type_code = a.trade_type_code
         AND    b.trade_type_code = :TRADE_TYPE_CODE
         AND    b.eparchy_code = a.trade_eparchy_code
         AND    b.limit_attr = '1'
         AND    b.limit_tag = '0'
         AND    SYSDATE BETWEEN b.start_date AND b.end_date
         AND    ROWNUM <= 1) a, td_s_tradetype b
 WHERE  b.trade_type_code = a.trade_type_code
 AND    b.eparchy_code = a.trade_eparchy_code
 AND    SYSDATE BETWEEN b.start_date AND b.end_date
 AND    ROWNUM <= 1