SELECT T.PARTITION_ID,
       T.USER_ID,
       T.USER_ID_A,
       T.RES_TYPE_CODE,
       T.RES_CODE,
       T.IMSI,
       T.KI,
       T.INST_ID,
       T.CAMPN_ID,
       TO_CHAR(T.START_DATE, 'yyyy-MM-dd HH24:MI:SS') START_DATE,
       TO_CHAR(T.END_DATE, 'yyyy-MM-dd HH24:MI:SS') END_DATE,
       T.REMARK,
       T.RSRV_STR1,
       T.RSRV_STR2,
       T.RSRV_STR3,
       T.RSRV_STR4,
       T.RSRV_STR5,
       T.RSRV_NUM1,
       T.RSRV_NUM5
  FROM TF_F_USER_RES T
 WHERE T.USER_ID = :USER_ID
   AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)
   AND T.RES_TYPE_CODE = :RES_TYPE_CODE
   AND T.RSRV_TAG1 = :RSRV_TAG1
   AND SYSDATE BETWEEN T.START_DATE AND T.END_DATE