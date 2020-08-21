DELETE FROM tf_f_user_plat_order a
 WHERE partition_id = MOD(:USER_ID,10000)
   AND user_id = :USER_ID
   AND start_date > end_date
   AND EXISTS (SELECT 1 FROM tf_b_trade_plat_order
                WHERE partition_id = MOD(:TRADE_ID,10000)
                  AND trade_id = :TRADE_ID
                  AND user_id = a.user_id
                  AND biz_code = a.biz_code)