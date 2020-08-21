SElECT K.BUSIFORM_NODE_ID,
               D.TRADE_ID,
               D.PRODUCT_NO,
               d.busi_state detail_busi_state,
               k.Node_Id,
               T.EPARCHY_CODE,
               k.CREATE_DATE INSERT_TIME,
               k.FLOW_EXPECT_TIME PLAN_FINISH_TIME,
               k.FLOW_REAL_TIME DEAL_TIME,
               '未完成' FLOW_STATE_DESC,
               '未完成' NODE_STATE,
               '1' NODE_DEAL_STATE,
               T.BPM_TEMPLET_ID
          FROM tf_b_ewe  T,
               tf_b_ewe_node_tra  K,
               tf_b_eop_eoms_detail D
         WHERE T.BPM_TEMPLET_ID in
               ('EDIRECTLINECHANGEINCOME',
                'EDIRECTLINECHANGEDLINENEW',
                'EDIRECTLINEOPENNEW',
                'EDIRECTLINECANCELNEW',
                'DIRECTLINECHANGEDLINENEW',
                'DIRECTLINEOPENNEW',
                'DIRECTLINECANCELNEW',
                'DIRECTLINECHANGEINCOME')
           AND T.BUSIFORM_ID = K.BUSIFORM_ID
           AND T.BI_SN = D.IBSYSID
           AND D.PRODUCT_NO = :PRODUCT_NO
           AND T.BUSIFORM_ID = :BUSIFORM_ID
 order by DEAL_TIME asc