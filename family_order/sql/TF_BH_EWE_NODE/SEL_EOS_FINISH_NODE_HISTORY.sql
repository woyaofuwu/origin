 SElECT K.BUSIFORM_NODE_ID,
       D.TRADE_ID,
       D.PRODUCT_NO,
       d.busi_state detail_busi_state,
       k.Node_Id,
       T.EPARCHY_CODE,
       k.CREATE_DATE INSERT_TIME,
       k.FLOW_EXPECT_TIME PLAN_FINISH_TIME,
       k.FLOW_REAL_TIME DEAL_TIME,
       '已完成' FLOW_STATE_DESC,
       '已完成' NODE_STATE,
       '2' NODE_DEAL_STATE,
       T.BPM_TEMPLET_ID
  FROM TF_BH_EWE T,
       TF_BH_EWE_NODE K,
       TF_BH_EOP_EOMS_DETAIL D
 WHERE T.BPM_TEMPLET_ID in ('EDIRECTLINECHANGEINCOME',
                             'EDIRECTLINECHANGEDLINENEW',
                             'EDIRECTLINEOPENNEW',
                             'EDIRECTLINECANCELNEW',
                             'DIRECTLINECHANGEDLINENEW',
                             'DIRECTLINEOPENNEW',
                             'DIRECTLINECANCELNEW',
                             'DIRECTLINECHANGEINCOME')
   AND T.BI_SN = D.IBSYSID
   AND T.BUSIFORM_ID = K.BUSIFORM_ID
   AND T.BUSIFORM_ID = :BUSIFORM_ID
   AND D.PRODUCT_NO = :PRODUCT_NO
 order by DEAL_TIME desc