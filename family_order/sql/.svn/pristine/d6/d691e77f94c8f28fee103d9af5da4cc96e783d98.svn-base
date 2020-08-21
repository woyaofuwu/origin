SELECT T.PARTITION_ID,
       to_char(T.USER_ID) user_ID,
       INST_TYPE,
       T.INST_ID,
       ATTR_CODE,
       ATTR_VALUE,
       to_char(T.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       to_char(T.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       to_char(T.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       T.UPDATE_STAFF_ID,
       T.UPDATE_DEPART_ID,
       T.REMARK,
       T.RSRV_NUM1,
       T.RSRV_NUM2,
       T.RSRV_NUM3,
       to_char(T.RSRV_NUM4) RSRV_NUM4,
       to_char(T.RSRV_NUM5) RSRV_NUM5,
       T.RSRV_STR1,
       T.RSRV_STR2,
       T.RSRV_STR3,
       T.RSRV_STR4,
       T.RSRV_STR5,
       to_char(T.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,
       to_char(T.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,
       to_char(T.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,
       T.RSRV_TAG1,
       T.RSRV_TAG2,
       T.RSRV_TAG3
  FROM tf_f_user_ATTR T
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   AND sysdate BETWEEN start_date AND end_date
   AND T.INST_TYPE = 'S'
   AND EXISTS (SELECT 1
          FROM TF_F_USER_SVC b
         WHERE b.USER_ID = TO_NUMBER(:USER_ID)
           AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
           AND b.inst_id = t.rela_inst_id
           AND b.service_id = :SERVICE_ID)