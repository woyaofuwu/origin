select D.TRADE_ID,
       D.USER_ID,
       D.RSRV_STR1,
       D.FEE_MODE,
       D.FEE_TYPE_CODE,
       D.FEE
  from TF_B_TRADEFEE_SUB D
 where D.TRADE_ID = :TRADE_ID
   and D.RSRV_STR1 = :ELEMENT_ID