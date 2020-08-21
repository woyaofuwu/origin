UPDATE TF_B_TRADE_PLAT_REGISTER
   SET START_DATE = to_date(:ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss')
 WHERE PARTITION_ID = MOD(:TRADE_ID,10000)
   AND TRADE_ID = to_number(:TRADE_ID)
   AND Exists(SELECT 1 FROM tf_b_trade_predeal
               WHERE trade_id = :TRADE_ID
                 AND subscribe_state='0')