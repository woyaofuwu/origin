SElECT   E.record_num,
	       W.BUSIFORM_ID,
	       D.TRADE_ID,
         D.PRODUCT_NO,
         D.busi_state detail_busi_state,
         T.IBSYSID,
         T.EPARCHY_CODE,
         E.PRODUCT_NAME,
         t.BUSI_TYPE,
         '已完成' FLOW_STATE_DESC,
         '已完成' NODE_STATE,
         T.GROUP_ID,
         T.CUST_NAME,
         W.ACCEPT_STAFF_ID TRADE_STAFF_ID,
	 W.BUSIFORM_OPER_TYPE,
         T.BPM_TEMPLET_ID
    FROM  TF_BH_EOP_SUBSCRIBE T,
      TF_BH_EOP_EOMS_DETAIL D,
      TF_BH_EOP_PRODUCT E,
      TF_BH_EWE W
   WHERE 
         T.BPM_TEMPLET_ID in
         ('EDIRECTLINECANCELNEW',
          'EDIRECTLINECHANGEDLINENEW',
          'EDIRECTLINECHANGEINCOME',
          'EDIRECTLINEOPENNEW')
   AND T.IBSYSID = D.IBSYSID
   AND T.IBSYSID = E.IBSYSID
   AND T.IBSYSID = W.BI_SN
   AND E.record_num = D.record_num
   AND W.ACCEPT_STAFF_ID = :STAFF_ID
   AND T.IBSYSID = :IBSYSID
   AND W.templet_type = '0'
   AND W.BUSIFORM_OPER_TYPE = :BUSIFORM_OPER_TYPE
   AND E.PRODUCT_ID = :PRODUCT_ID
   AND T.CUST_NAME like '%' || :CUST_NAME || '%'
   AND T.GROUP_ID = :GROUP_ID
   AND T.EPARCHY_CODE = :EPARCHY_CODE
   AND T.ACCEPT_TIME > to_date(:START_DATE, 'yyyy-MM-dd  HH24:mi:ss')
   AND T.ACCEPT_TIME < to_date(:END_DATE, 'yyyy-MM-dd  HH24:mi:ss')
   AND D.PRODUCT_NO like '%' || :PRODUCT_NO || '%'
  order by t.ACCEPT_TIME desc