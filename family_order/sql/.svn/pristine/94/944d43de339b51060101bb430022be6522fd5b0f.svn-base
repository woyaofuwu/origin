UPDATE tf_b_trade_plat_order
   SET start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'),rsrv_num1=:RSRV_NUM1,oper_code=:OPER_CODE  
 WHERE partition_id=MOD(TO_NUMBER(:TRADE_ID),10000)
   AND trade_id=TO_NUMBER(:TRADE_ID)
   AND user_id=TO_NUMBER(:USER_ID)
   AND biz_code=:BIZ_CODE
   AND sp_code=:SP_CODE
   AND oper_code=:OPER_CODE