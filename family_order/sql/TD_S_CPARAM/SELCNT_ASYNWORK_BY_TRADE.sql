select count(1) recordcount
  from ti_b_asynwork
 where TRADE_ID=TO_NUMBER(:TRADE_ID)
   AND MONTH=TO_NUMBER(SUBSTR(:TRADE_ID,5,2))