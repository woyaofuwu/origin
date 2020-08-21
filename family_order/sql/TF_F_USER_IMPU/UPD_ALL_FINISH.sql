UPDATE TF_F_USER_IMPU
   SET TEL_URL      = :TEL_URL,
       SIP_URL      = :SIP_URL,
       IMPI         = :IMPI,
       IMS_USER_ID  = :IMS_USER_ID,
       IMS_PASSWORD = :IMS_PASSWORD,
       END_DATE     = to_date(:END_DATE, 'yyyy-mm-dd hh24:mi:ss'),
       RSRV_STR1    = :RSRV_STR1,
       RSRV_STR2    = :RSRV_STR2,
       RSRV_STR3    = :RSRV_STR3,
       RSRV_STR4    = :RSRV_STR4,
       RSRV_STR5    = :RSRV_STR5
 WHERE PARTITION_ID = MOD(to_number(:USER_ID), 10000)
   and USER_ID = to_number(:USER_ID)
   and START_DATE = to_date(:START_DATE, 'yyyy-mm-dd hh24:mi:ss')
   and rsrv_str1 = :RSRV_STR1