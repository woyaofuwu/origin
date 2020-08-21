DELETE FROM tf_f_user_svc a
 WHERE a.partition_id = MOD(to_number(:USER_ID), 10000)
   AND a.user_id = :USER_ID 
   AND EXISTS (SELECT 1 FROM tf_b_trade_svc_bak b
                WHERE b.trade_id = :TRADE_ID
                  AND b.user_id = a.user_id 
                  AND b.service_id = a.service_id
                  AND b.start_date = a.start_date)