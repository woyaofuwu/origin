Select  T1.PARTITION_ID,
         TO_CHAR(T1.USER_ID) USER_ID,
         T1.RSRV_VALUE_CODE,
         T1.RSRV_VALUE,
         T1.RSRV_STR1,
         T1.RSRV_STR2,
         T1.RSRV_STR3,
         T1.RSRV_STR4,
         T1.RSRV_STR5,
         T1.RSRV_STR6,
         T1.RSRV_STR7,
         T1.RSRV_STR8,
         T1.RSRV_STR9,
         T1.RSRV_TAG1,
         T1.RSRV_TAG2,
         T1.RSRV_TAG3,
         T1.RSRV_TAG4,
         T1.PROCESS_TAG,
         T1.STAFF_ID,
         T1.DEPART_ID,
         T1.TRADE_ID,
         TO_CHAR(T1.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
         TO_CHAR(T1.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
         T1.UPDATE_TIME,
         T1.UPDATE_STAFF_ID,
         T1.UPDATE_DEPART_ID,
         T1.REMARK,
         TO_CHAR(T1.INST_ID) INST_ID
 From TF_F_USER_OTHER T1
 where t1.rsrv_value_code='FTTH_GROUP'
  and T1.RSRV_TAG2= '1'
  and (T1.User_Id = :USER_ID1 OR T1.user_id = :USER_ID2)
 AND (TRIM(T1.RSRV_STR1) = :RSRV_STR1 OR :RSRV_STR1 IS NULL)
  AND SYSDATE BETWEEN T1.START_DATE AND T1.END_DATE
 AND T1.RSRV_STR3=:KD_NUMBER
 AND T1.RSRV_STR4=:SERIAL_NUMBER