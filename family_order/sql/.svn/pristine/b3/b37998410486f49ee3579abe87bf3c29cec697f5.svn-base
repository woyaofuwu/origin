UPDATE tf_f_user_svcstate a
   SET end_date         = To_Date(:END_DATE, 'yyyy-mm-dd HH24:MI:SS'),
       update_time      = SYSDATE,
       update_staff_id  = :UPDATE_STAFF_ID,
       update_depart_id = :UPDATE_DEPART_ID
 WHERE user_id = to_number(:USER_ID)
   AND partition_id = MOD(to_number(:USER_ID), 10000)
   AND SYSDATE BETWEEN start_date AND end_date
   AND EXISTS
 (SELECT 1
          FROM tf_b_trade_svcstate
         WHERE trade_id = to_number(:TRADE_ID)
           AND accept_month = to_number(substr(:TRADE_ID, 5, 2))
           AND user_id = a.user_id
           AND service_id = a.service_id
           AND state_code != a.state_code
           AND modify_tag = '0')