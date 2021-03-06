SElECT D.TRADE_ID,
   D.PRODUCT_NO,
   D.BUSI_STATE,
   T.EPARCHY_CODE,
   '1' NODE_DEAL_STATE,
   T.IBSYSID,
   E.PRODUCT_NAME,
   T.BUSI_TYPE,
   'δ���' FLOW_STATE_DESC,
   'δ���' NODE_STATE,
   T.BPM_TEMPLET_ID,
   T.ACCEPT_TIME
FROM  TF_B_EOP_SUBSCRIBE  T,
      TF_B_EOP_EOMS_DETAIL D,
      TF_B_EOP_PRODUCT E
WHERE T.BPM_TEMPLET_ID in ('EDIRECTLINECANCELNEW',
                            'EDIRECTLINECHANGEDLINENEW',
                            'EDIRECTLINECHANGEINCOME',
                            'EDIRECTLINEOPENNEW')
and T.IBSYSID = D.IBSYSID
and E.IBSYSID = T.IBSYSID
and T.IBSYSID = :IBSYSID
and E.PRODUCT_ID = :PRODUCT_ID
and T.EPARCHY_CODE = :EPARCHY_CODE
and T.GROUP_ID = :GROUP_ID
and E.record_num = D.record_num
and T.ACCEPT_TIME > to_date(:BEGIN_DATE,'yyyy-MM-dd  HH24:mi:ss')
and T.ACCEPT_TIME  < to_date(:END_DATE,'yyyy-MM-dd  HH24:mi:ss')
order by ACCEPT_TIME desc