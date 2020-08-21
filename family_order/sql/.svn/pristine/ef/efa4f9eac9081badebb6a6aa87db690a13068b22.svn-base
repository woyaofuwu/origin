select t.* 
    from TF_F_TERMINALORDER t
   where t.order_id=:NET_ORDER_ID
     and t.PRODUCT_ID =:PRODUCT_ID
     and t.PACKAGE_ID =:PACKAGE_ID
     and t.SERIAL_NUMBER=:SERIAL_NUMBER
     and t.ORDER_STATE = :ORDER_STATE
		 AND T.RSRV_STR2=:RSRV_STR2
		 AND T.ORDER_TYPE IN ('1','3','5','6','7')
     and sysdate < t.end_time