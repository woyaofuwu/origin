select to_char(SHARE_ID) SHARE_ID,
               PARTITION_ID,
               to_char(USER_ID) USER_ID,
               to_char(RELA_INST_ID) RELA_INST_ID,
               DISCNT_CODE,
               DISCNT_CODE_A,
               to_char(SHARE_INST_ID) SHARE_INST_ID,
               SHARE_TYPE ,
               to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
               to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
               to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
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
               to_char(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,
               to_char(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,
               to_char(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,
               RSRV_TAG1,
               RSRV_TAG2,
               RSRV_TAG3
          from TF_F_USER_SHARE
         where user_id = to_number(:USER_ID)
           AND partition_id = MOD(to_number(:USER_ID), 10000)
           and SHARE_ID=to_number(:SHARE_ID)
           and end_date > sysdate
           and months_between(end_date, sysdate) > 1