select PARTITION_ID,
       INST_ID,
       USER_ID,
       SHARE_INST_ID,
       SHARE_WAY,
       SHARE_TYPE_CODE,
       SHARE_LIMIT,
       START_DATE,
       END_DATE,
       UPDATE_TIME,
       UPDATE_STAFF_ID,
       UPDATE_DEPART_ID,
       REMARK,
       RSRV_NUM1,
       RSRV_NUM2,
       RSRV_NUM3,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       RSRV_DATE1,
       RSRV_DATE2,
       RSRV_TAG1,
       RSRV_TAG2
  from TF_F_USER_SHARE_INFO
 where user_id = to_number(:USER_ID)
   and end_date > sysdate
   and months_between(end_date, sysdate) > 1
