select PARTITION_ID,
       USER_ID,
       USER_ID_A,
       DISCNT_CODE,
       SPEC_TAG,
       RELATION_TYPE_CODE,
       INST_ID,
       CAMPN_ID,
       START_DATE,
       END_DATE,
       UPDATE_TIME,
       UPDATE_STAFF_ID,
       UPDATE_DEPART_ID,
       REMARK,
       RSRV_NUM1,
       RSRV_NUM2,
       RSRV_NUM3,
       RSRV_NUM4,
       RSRV_NUM5,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       RSRV_STR4,
       RSRV_STR5,
       RSRV_DATE1,
       RSRV_DATE2,
       RSRV_DATE3,
       RSRV_TAG1,
       RSRV_TAG2
  from tf_f_user_discnt
 where partition_id = mod(:USER_ID,10000)
   and user_id = to_number(:USER_ID)
   and end_date > sysdate
   and discnt_code = :DISCNT_CODE
   and end_date > sysdate
