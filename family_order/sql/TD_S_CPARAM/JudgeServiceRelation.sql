SELECT COUNT(1) recordcount
  FROM tf_f_user_svc a
 WHERE a.service_id = to_number(:SERVICE_ID_A)
   AND a.user_id = :USER_ID
   AND a.partition_id = MOD(:USER_ID, 10000)
   AND EXISTS (SELECT 1
          FROM tf_b_trade_svc b
         WHERE b.modify_tag = :MODIFY_TAG
           AND b.service_id = to_number(:SERVICE_ID_B)
           AND b.trade_id = to_number(:TRADE_ID))