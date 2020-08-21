select T.INST_ID,
       T.ACCEPT_MONTH,
       T.IBSYSID,
       T.BUSI_STATE,
       T.PRODUCT_NO,
       T.RECORD_NUM,
       T.SERIALNO,
       T.TRADE_ID,
       T.DEAL_TYPE,
       T.CREATE_DATE,
       T.REMARK,
       T.RSRV_STR1,
       T.RSRV_STR2,
       T.RSRV_STR3
  from TF_B_EOP_EOMS_STATE T
    WHERE T.SERIALNO = :SERIALNO
      and T.PRODUCT_NO = :PRODUCT_NO