SELECT a.PARTITION_ID,
       to_char(a.USER_ID) USER_ID,
       to_char(a.USER_ID_A) USER_ID_A,
       a.DISCNT_CODE,
       a.SPEC_TAG,
       a.RELATION_TYPE_CODE,
       to_char(a.INST_ID) INST_ID,
       to_char(a.CAMPN_ID) CAMPN_ID,
       to_char(a.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       to_char(a.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       to_char(a.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       a.UPDATE_STAFF_ID,
       a.UPDATE_DEPART_ID,
       a.REMARK,
       a.RSRV_NUM1,
       a.RSRV_NUM2,
       a.RSRV_NUM3,
       to_char(a.RSRV_NUM4) RSRV_NUM4,
       to_char(a.RSRV_NUM5) RSRV_NUM5,
       a.RSRV_STR1,
       a.RSRV_STR2,
       a.RSRV_STR3,
       a.RSRV_STR4,
       a.RSRV_STR5,
       to_char(a.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,
       to_char(a.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,
       to_char(a.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,
       a.RSRV_TAG1,
       a.RSRV_TAG2,
       a.RSRV_TAG3,
	   to_char(a.end_date + 1/24/60/60,'yyyy-mm-dd hh24:mi:ss') time_start_date,
	   to_char(add_months(a.end_date,12),'yyyy-mm-dd hh24:mi:ss') time_end_date,
       case
         when a.end_date < to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss') then
          0
         else
          1
       end timeflag,
	   c.PARA_CODE2
  FROM tf_f_user_discnt a, td_s_commpara c
 WHERE partition_id = mod(to_number(:USER_ID), 10000)
   AND user_id = TO_NUMBER(:USER_ID)
   AND a.end_date > sysdate
   AND c.subsys_code = 'CSM'
   AND c.param_attr = :PARAM_ATTR
   AND c.param_code = :PARAM_CODE
   AND c.para_code1 = a.discnt_code
   AND (c.eparchy_code = :EPARCHY_CODE OR c.eparchy_code = 'ZZZZ')
   AND sysdate BETWEEN c.start_date AND c.end_date
