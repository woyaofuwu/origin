 update TF_B_ORDER_PRE D
    set D.ORDER_ID     = :ORDER_ID,
        D.END_DATE     = sysdate,
        D.REPLY_STATE  = :REPLY_STATE,
        D.ACCEPT_STATE = :ACCEPT_STATE
  where D.PRE_ID = :PRE_ID