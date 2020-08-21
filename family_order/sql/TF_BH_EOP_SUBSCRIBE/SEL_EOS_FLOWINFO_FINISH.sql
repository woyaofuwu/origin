SElECT D.TRADE_ID,
       D.PRODUCT_NO,
       D.BUSI_STATE,
	   W.BUSIFORM_ID,
       T.IBSYSID,
       T.EPARCHY_CODE,
       '2' NODE_DEAL_STATE,
       E.PRODUCT_NAME,
       T.BUSI_TYPE,
       '已完成' FLOW_STATE_DESC,
       '已完成' NODE_STATE,
       T.BPM_TEMPLET_ID,
	   T.ACCEPT_TIME
  FROM TF_BH_EOP_SUBSCRIBE T,
       TF_BH_EOP_EOMS_DETAIL	D,
	   TF_BH_EWE             W,
	   TF_BH_EOP_PRODUCT E
 WHERE T.BPM_TEMPLET_ID in ('EDIRECTLINECANCELNEW',
                            'EDIRECTLINECHANGEDLINENEW',
                            'EDIRECTLINECHANGEINCOME',
                            'EDIRECTLINEOPENNEW')
   AND T.IBSYSID = D.IBSYSID
   AND E.IBSYSID = T.IBSYSID
   AND T.IBSYSID = W.BI_SN
   AND W.templet_type = '0'
   AND T.IBSYSID = :IBSYSID
   AND E.PRODUCT_ID = :PRODUCT_ID
   AND T.GROUP_ID = :GROUP_ID
   AND T.EPARCHY_CODE = :EPARCHY_CODE
   and E.record_num = D.record_num
   AND T.ACCEPT_TIME > to_date(:BEGIN_DATE, 'yyyy-MM-dd  HH24:mi:ss')
   AND T.ACCEPT_TIME < to_date(:END_DATE, 'yyyy-MM-dd  HH24:mi:ss')
 order by T.ACCEPT_TIME desc