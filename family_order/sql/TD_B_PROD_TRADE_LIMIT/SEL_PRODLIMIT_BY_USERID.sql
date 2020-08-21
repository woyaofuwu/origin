SELECT pid,
       id_type,
       trade_type_code,
       months,
       to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       eparchy_code,
       to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date,
       to_char(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time
  FROM td_b_prod_trade_limit a
 WHERE trade_type_code = :TRADE_TYPE_CODE
   AND id_type = '0'
   AND pid IN
       (SELECT product_id
          FROM tf_f_user_infochange b
         WHERE user_id = :USER_ID
           AND partition_id = MOD(to_number(:USER_ID), 10000)
           AND SYSDATE BETWEEN start_date + 0 AND end_date + 0
            )
   AND (eparchy_code = :EPARCHY_CODE or eparchy_code = 'ZZZZ')
   AND sysdate BETWEEN start_date AND end_date
