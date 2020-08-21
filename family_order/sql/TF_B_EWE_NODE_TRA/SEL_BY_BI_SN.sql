SElECT 	       T.BI_SN,
               K.SUB_BI_SN,
               K.BUSIFORM_NODE_ID,
               k.Node_Id, 
               T.EPARCHY_CODE,
               k.CREATE_DATE INSERT_TIME,
               k.FLOW_EXPECT_TIME PLAN_FINISH_TIME,
               k.DEAL_STAFF_ID,
               k.UPDATE_STAFF_ID,
               to_char(k.FLOW_REAL_TIME,'yyyy-mm-dd hh24:mi:ss') as DEAL_TIME,
               '未完成' FLOW_STATE_DESC,
               '未完成' NODE_STATE,
               '1' NODE_DEAL_STATE,
               T.BPM_TEMPLET_ID
          FROM tf_b_ewe  T,
               tf_b_ewe_node_tra  K
           where T.BUSIFORM_ID = K.BUSIFORM_ID
           AND T.BI_SN = :BI_SN
 order by k.update_date  asc