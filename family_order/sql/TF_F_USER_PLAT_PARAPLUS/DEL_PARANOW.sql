DELETE tf_f_user_plat_paraplus a 
 WHERE partition_id = MOD(:USER_ID,10000)
   AND user_id = :USER_ID
   AND EXISTS (SELECT 1 FROM tf_b_trade_plat_paraplus
                WHERE partition_id = MOD(:TRADE_ID,10000)
                  AND trade_id = :TRADE_ID
                  AND user_id = a.user_id
                  AND info_type = a.Info_Type
                  AND info_tag = a.Info_Tag
                  AND info_code = a.Info_Code)