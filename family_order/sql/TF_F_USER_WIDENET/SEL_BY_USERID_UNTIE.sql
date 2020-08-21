SELECT a.PARTITION_ID,
       TO_CHAR(a.USER_ID) USER_ID,
       a.STAND_ADDRESS,
       a.DETAIL_ADDRESS,
       a.SIGN_PATH,
       a.PORT_TYPE,
       a.INST_ID,
       TO_CHAR(a.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       TO_CHAR(a.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       a.UPDATE_TIME,
       a.UPDATE_STAFF_ID,
       a.UPDATE_DEPART_ID,
       a.REMARK,
       a.RSRV_NUM1,
       a.RSRV_NUM2,
       a.RSRV_NUM3,
       a.RSRV_NUM4,
       a.RSRV_NUM5,
       a.RSRV_STR1,
       a.RSRV_STR2,
       a.RSRV_STR3,
       a.RSRV_STR4,
       a.RSRV_STR5,
       TO_CHAR(a.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,
       TO_CHAR(a.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,
       TO_CHAR(a.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,
       a.RSRV_TAG1,
       a.RSRV_TAG2,
       a.RSRV_TAG3,
       a.ACCT_PASSWD,
       a.STAND_ADDRESS_CODE,
       a.PHONE,
       a.CONTACT,
       a.CONTACT_PHONE,
       b.rsrv_str1 IP_ADDRESS,
       b.main_tag,
       b.acct_id
  FROM TF_F_USER_WIDENET a,TF_F_USER_WIDENET_ACT b
 WHERE a.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)
   AND a.USER_ID = TO_NUMBER(:USER_ID)
   AND SYSDATE BETWEEN a.START_DATE AND a.END_DATE
   and b.PARTITION_ID(+) = MOD(TO_NUMBER(:USER_ID), 10000)
   AND b.USER_ID(+) = TO_NUMBER(:USER_ID)
   and a.USER_ID = b.USER_ID(+)
   and b.end_date>sysdate