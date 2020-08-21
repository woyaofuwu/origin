update tf_b_trade_other set RSRV_TAG1=:RSRV_TAG1
where  trade_id=TO_NUMBER(:TRADE_ID)
and  ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
and user_id=:USER_ID