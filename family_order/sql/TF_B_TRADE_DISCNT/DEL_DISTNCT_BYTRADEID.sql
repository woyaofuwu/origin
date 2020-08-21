delete from TF_B_TRADE_DISCNT b 
  where b.trade_id=TO_NUMBER(:TRADE_ID)
    and  b.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
  and RSRV_STR3='M'