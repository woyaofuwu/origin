SELECT IBSYSID�� RECORD_NUM�� ACCEPT_MONTH�� BUSI_STATE�� DEAL_STATE�� PRODUCT_NO�� TRADE_ID�� MODIFY_TAG�� INSERT_TIME�� REMARK�� RSRV_STR1�� RSRV_STR2�� RSRV_STR3
  FROM TF_B_EOP_EOMS_DETAIL A
 WHERE 1 = 1
   AND A.IBSYSID = :IBSYSID
