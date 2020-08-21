SELECT COUNT(1) recordcount
  from tf_b_trade t
 where t.user_id = :USER_ID
   and t.trade_type_code in ('3034', '3035', '3037')
   and t.exec_time > sysdate
   and :IF_BOOKING = 'true'
   and (:TRADE_TYPE_CODE in ('3037', '3035') or
       ((:TRADE_TYPE_CODE = '3034' and t.trade_type_code = '3037' and
       to_char(t.exec_time, 'yyyy-mm-dd hh24:mi:ss') >
       to_char(last_day(sysdate) + 1, 'yyyy-mm-dd') || ' 01:00:00') or
       t.user_id_b = :USER_ID_B))