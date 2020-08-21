SELECT T.PARENT_BPM_TEMPLET_ID,
       T.PARENT_NODE_ID,
       T.BPM_TEMPLET_ID,
       T.VALID_TAG,
       T.IS_MULTI,
       T.RELA_SVC,
       T.ACCEPT_DEPART_ID,
       T.UPDATE_DEPART_ID,
       T.UPDATE_STAFF_ID,
       T.ACCEPT_STAFF_ID,
       T.CREATE_DATE,
       T.UPDATE_DATE
  FROM TD_B_EWE_SUB_RELA T
 WHERE T.PARENT_BPM_TEMPLET_ID = :PARENT_BPM_TEMPLET_ID
   AND T.PARENT_NODE_ID = :PARENT_NODE_ID