SELECT A.*, B.*, B.SERVICENO SERIAL_NUMBER, B.RSRV_STR4 SUB_ORDER_RSRV_STR4
  FROM TF_B_CTRM_GERLORDER A, TF_B_CTRM_GERLSUBORDER B
 WHERE A.ORDER_ID = B.ORDER_ID
   AND B.STATE IN ('TD', 'SE', 'AF')