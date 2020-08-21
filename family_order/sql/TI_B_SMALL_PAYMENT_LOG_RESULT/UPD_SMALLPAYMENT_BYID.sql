UPDATE TI_B_SMALL_PAYMENT_LOG_RESULT 
SET
     CANCEL_FLAG=:CANCEL_FLAG,
     BACK_FEE_TIME=sysdate,
     trade_staff_id=:TRADE_STAFF_ID,
     trade_depart_id=:TRADE_DEPART_ID
 WHERE intf_trade_id=:INTF_TRADE_ID