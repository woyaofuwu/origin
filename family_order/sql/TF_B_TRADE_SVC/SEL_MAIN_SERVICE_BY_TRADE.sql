SELECT to_char(trade_id) trade_id,
       accept_month,
       to_char(user_id) user_id,
       service_id,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       RSRV_STR4,
       RSRV_STR5,
       RSRV_STR6,
       RSRV_STR7,
       RSRV_STR8, 
       modify_tag,
       to_char(start_date,
               'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(end_date,
               'yyyy-mm-dd hh24:mi:ss') end_date
  FROM tf_b_trade_svc
 WHERE trade_id = to_number(:TRADE_ID)
   AND accept_month = :ACCEPT_MONTH
   AND modify_tag = :MODIFY_TAG
   AND service_id IN
       (SELECT service_id
          FROM td_b_product_svc a
         WHERE a.product_id = :PRODUCT_ID
           AND a.main_tag = :MAIN_TAG
           AND SYSDATE BETWEEN a.start_date AND a.end_date)