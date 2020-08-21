DELETE FROM tf_f_user_infochange a
 WHERE a.user_id=TO_NUMBER(:USER_ID)
   AND a.partition_id = MOD(to_number(:USER_ID), 10000)
   AND EXISTS (SELECT 1 FROM tf_b_trade_infochange_bak b
                WHERE b.trade_id = :TRADE_ID
                  AND b.user_id = a.user_id
                  AND b.start_date = a.start_date)