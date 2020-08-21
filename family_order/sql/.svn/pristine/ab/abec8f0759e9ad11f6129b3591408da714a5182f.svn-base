DELETE tf_f_user_plat_order a
 WHERE partition_id = MOD(:USER_ID,10000)
   AND user_id = :USER_ID
   AND EXISTS (SELECT 1 FROM tf_b_trade_plat_order
                WHERE Partition_id = MOD(:TRADE_ID,10000)
                  AND trade_id = :TRADE_ID
                  AND user_id = a.user_id
                  AND biz_code = a.biz_code
                  AND SYSDATE BETWEEN a.start_date AND a.end_date
                  AND (oper_code = '06' OR oper_code = '11'))