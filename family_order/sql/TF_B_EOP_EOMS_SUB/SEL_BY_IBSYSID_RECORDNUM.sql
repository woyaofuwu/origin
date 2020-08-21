select C.SUB_IBSYSID,
       C.IBSYSID,
       C.SEQ,
       C.GROUP_SEQ,
       C.NODE_ID,
       C.ACCEPT_MONTH,
       C.ATTR_CODE,
       C.ATTR_NAME,
       C.ATTR_VALUE,
       C.ATTR_TYPE,
       C.PARENT_ATTR_CODE,
       C.RECORD_NUM,
       C.UPDATE_TIME,
       C.RSRV_STR1,
       C.RSRV_STR2
  from TF_B_EOP_EOMS_SUB C
 where C.IBSYSID = :IBSYSID
   AND C.RECORD_NUM = :RECORD_NUM