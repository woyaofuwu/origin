SELECT PARTITION_ID,
       to_char(USER_ID) USER_ID,
       to_char(USER_ID_A) USER_ID_A,
       DISCNT_CODE,
       SPEC_TAG,
       RELATION_TYPE_CODE,
       RSRV_NUM1,
       RSRV_NUM2,
       RSRV_NUM3,
       to_char(RSRV_NUM4) RSRV_NUM4,
       to_char(RSRV_NUM5) RSRV_NUM5,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       RSRV_STR4,
       RSRV_STR5,
	   to_char(d.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       to_char(d.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       to_char(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,
       to_char(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,
       to_char(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,
       RSRV_TAG1,
       RSRV_TAG2,
       RSRV_TAG3
  FROM tf_f_user_discnt d,td_s_commpara a
 WHERE D.user_id = TO_NUMBER(:USER_ID)
   AND D.partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   and a.param_attr = :PARAM_ATTR
   and a.param_code = :PARAM_CODE
   and D.discnt_code = a.para_code1
   and a.subsys_code = 'CSM'
   AND D.START_DATE < D.END_DATE
   AND SYSDATE < D.END_DATE
   
