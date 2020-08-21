INSERT INTO TF_F_USER_ATTR
  (PARTITION_ID,
   USER_ID,
   INST_TYPE,
   INST_ID,
   ATTR_CODE,
   ATTR_VALUE,
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
   RSRV_TAG2,
   RSRV_TAG3)
  SELECT MOD(to_number(:USER_ID), 10000),
         to_number(:USER_ID),
         'D',
         a.INST_ID,
         c.ATTR_CODE,
         c.ATTR_INIT_VALUE,
         a.START_DATE,
         a.END_DATE,
         SYSDATE,
         a.UPDATE_STAFF_ID,
         a.UPDATE_DEPART_ID,
         a.REMARK,
         a.discnt_code,
         NULL,
         NULL,
         NULL,
         NULL,
         NULL,
         NULL,
         NULL,
         NULL,
         NULL,
         NULL,
         NULL,
         NULL,
         NULL,
         NULL,
         NULL
    FROM tf_f_user_discnt a, TD_B_ATTR_ITEMA C
     WHERE a.discnt_code = c.id and c.id_type = 'D'
    AND   c.attr_can_null = '0'
    AND   a.USER_ID = to_number(:USER_ID)
    AND   a.PARTITION_ID = MOD(to_number(:USER_ID), 10000)
    AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE
   AND NOT EXISTS (SELECT 1 FROM TF_F_USER_ATTR
                      WHERE user_id = TO_NUMBER(:USER_ID)
                        AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
                        AND inst_type = 'D'
                        AND inst_id = a.inst_id
                        AND ATTR_CODE = c.ATTR_CODE
                        AND end_date > start_date
                        AND end_date > a.start_date)