SELECT A.GROUP_ID,
       A.USER_ID,
       A.ROLE_CODE,
       A.START_DATE,
       A.END_DATE,
       A.UPDATE_TIME,
       A.UPDATE_STAFF_ID,
       A.UPDATE_DEPART_ID
  FROM TF_F_USER_CLUSTER_RELA A
 WHERE A.GROUP_ID = :GROUP_ID
   AND END_DATE > SYSDATE