UPDATE tf_b_trade_plat_order
   SET first_date=TO_DATE(:FIRST_DATE, 'YYYY-MM-DD HH24:MI:SS'),first_date_mon=TO_DATE(:FIRST_DATE_MON, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE partition_id=MOD(TO_NUMBER(:TRADE_ID),10000)
   AND trade_id=TO_NUMBER(:TRADE_ID)
   AND user_id=TO_NUMBER(:USER_ID)
   AND biz_code=:BIZ_CODE
   AND sp_code=:SP_CODE
   AND oper_code=:OPER_CODE