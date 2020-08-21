select to_char(th.USER_ID) USER_ID,
       th.rsrv_value_code as ATTR_CODE,
       th.rsrv_value  as ATTR_VALUE,
       to_char(th.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       to_char(th.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       to_char(th.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       th.UPDATE_STAFF_ID,
       th.UPDATE_DEPART_ID,
       th.REMARK,
       th.RSRV_NUM1,
       th.RSRV_NUM2,
       th.RSRV_NUM3,
       to_char(th.RSRV_NUM4) RSRV_NUM4,
       to_char(th.RSRV_NUM5) RSRV_NUM5,
       th.RSRV_STR1,
       th.RSRV_STR2,
       th.RSRV_STR3,
       th.RSRV_STR4,
       th.RSRV_STR5,
       to_char(th.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,
       to_char(th.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,
       to_char(th.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,
       th.RSRV_TAG1,
       th.RSRV_TAG2,
       th.RSRV_TAG3
  from tf_f_user usr, tf_F_user_other th
 where usr.cust_id = :CUST_ID
   and usr.user_id = th.user_id
   and usr.partition_id = th.partition_id
   and usr.partition_id = mod(usr.user_id, 10000)
   and usr.remove_tag = 0
   and th.rsrv_value_code = 'MAS_ID'
   and th.end_date > sysdate