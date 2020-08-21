SELECT T.BUSIFORM_NODE_ID,
       T.BUSIFORM_ID,
       T.PRE_BUSIFORM_NODE_ID,
       T.ACCEPT_MONTH,
       T.NODE_ID,
       T.PRE_NODE_ID,
       T.SUB_BI_SN,
       T.REMARK,
       T.STATE,
       T.REC_EOS_ROLE_ID,
       T.DEAL_EOS_ROLE_ID,
       T.REC_STAFF_ID,
       T.DEAL_STAFF_ID,
       T.REC_EPARCHY_CODE,
       T.DEAL_EPARCHY_CODE,
       T.REC_DEPART_ID,
       T.DEAL_DEPART_ID,
       T.ACCEPT_DEPART_ID,
       T.UPDATE_DEPART_ID,
       T.UPDATE_STAFF_ID,
       T.ACCEPT_STAFF_ID,
       TO_CHAR(T.FLOW_EXPECT_TIME, 'YYYY-MM-DD HH24:MI:SS') FLOW_EXPECT_TIME,
       TO_CHAR(T.FLOW_REAL_TIME, 'YYYY-MM-DD HH24:MI:SS') FLOW_REAL_TIME,
       TO_CHAR(T.AUTO_TIME, 'YYYY-MM-DD HH24:MI:SS') AUTO_TIME,
       TO_CHAR(T.CREATE_DATE, 'YYYY-MM-DD HH24:MI:SS') CREATE_DATE,
       TO_CHAR(T.UPDATE_DATE, 'YYYY-MM-DD HH24:MI:SS') UPDATE_DATE
  FROM TF_BH_EWE_NODE T
 WHERE T.BUSIFORM_ID = :BUSIFORM_ID