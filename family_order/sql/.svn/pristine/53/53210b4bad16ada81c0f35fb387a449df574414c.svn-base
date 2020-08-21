SELECT A.PARTITION_ID,
       A.USER_ID,
       A.USER_ID_A,
       A.SERVICE_ID,
       A.MAIN_TAG,
       A.INST_ID,
       A.CAMPN_ID,
       TO_CHAR(A.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       TO_CHAR(A.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       TO_CHAR(A.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       A.UPDATE_STAFF_ID,
       A.UPDATE_DEPART_ID,
       A.REMARK,
       A.RSRV_NUM1,
       A.RSRV_NUM2,
       A.RSRV_NUM3,
       A.RSRV_NUM4,
       A.RSRV_NUM5,
       A.RSRV_STR1,
       A.RSRV_STR2,
       A.RSRV_STR3,
       A.RSRV_STR4,
       A.RSRV_STR5,
       A.RSRV_DATE1,
       A.RSRV_DATE2,
       A.RSRV_DATE3,
       A.RSRV_TAG1,
       A.RSRV_TAG2,
       A.RSRV_TAG3,
       A.RSRV_STR6,
       A.RSRV_STR7,
       A.RSRV_STR8,
       A.RSRV_STR9,
       A.RSRV_STR10,
       B.USER_ID_A,
       B.SERIAL_NUMBER_A,
       B.USER_ID_B,
       B.SERIAL_NUMBER_B,
       B.SHORT_CODE
  FROM TF_F_USER_SVC A, TF_F_RELATION_UU B
 WHERE A.USER_ID = B.USER_ID_B
   AND A.PARTITION_ID = B.PARTITION_ID
   AND A.SERVICE_ID = 831
   AND A.END_DATE > SYSDATE
   AND B.RELATION_TYPE_CODE = '45'
   AND B.USER_ID_A = :USER_ID_A
   AND B.END_DATE > SYSDATE
