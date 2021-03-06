SELECT T.LOG_ID,
       T.BUSIFORM_NODE_ID,
       T.BUSIFORM_ID,
       T.ACCEPT_MONTH,
       T.STEP_ID,
       T.LOG_INFO,
       T.LOG_INFO1,
       T.LOG_INFO2,
       T.LOG_INFO3,
       T.LOG_INFO4,
       T.VALID_TAG,
       T.ACCEPT_DEPART_ID,
       T.UPDATE_DEPART_ID,
       T.UPDATE_STAFF_ID,
       T.ACCEPT_STAFF_ID,
       TO_CHAR(T.CREATE_DATE, 'YYYY-MM-DD HH24:MI:SS') CREATE_DATE,
       TO_CHAR(T.UPDATE_DATE, 'YYYY-MM-DD HH24:MI:SS') UPDATE_DATE
  FROM TF_B_EWE_ERROR_LOG T
 WHERE T.BUSIFORM_NODE_ID = :BUSIFORM_NODE_ID
