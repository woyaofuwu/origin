UPDATE tf_b_trade_plat_order
   SET biz_state_code=:BIZ_STATE_CODE,oper_code=:OPER_CODE  
 WHERE partition_id = MOD(:TRADE_ID,10000)
   AND trade_id = :TRADE_ID