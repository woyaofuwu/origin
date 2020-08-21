select t.PARTITION_ID, to_char(t.USER_ID) USER_ID, to_char(t.USER_ID_A) USER_ID_A, t.RES_TYPE_CODE, t.RES_CODE, t.IMSI, t.KI, to_char(t.INST_ID) INST_ID, to_char(t.CAMPN_ID) CAMPN_ID, to_char(t.START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, to_char(t.END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, to_char(t.UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, t.UPDATE_STAFF_ID, t.UPDATE_DEPART_ID, t.REMARK, t.RSRV_NUM1, t.RSRV_NUM2, t.RSRV_NUM3, to_char(t.RSRV_NUM4) RSRV_NUM4, to_char(t.RSRV_NUM5) RSRV_NUM5, t.RSRV_STR1, t.RSRV_STR2, t.RSRV_STR3, t.RSRV_STR4, t.RSRV_STR5, to_char(t.RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, to_char(t.RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, to_char(t.RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, t.RSRV_TAG1, t.RSRV_TAG2, t.RSRV_TAG3
 from tf_f_user_res t
 where t.user_id=:USER_ID
      and t.partition_id=mod(:USER_ID,10000)
      and t.user_id_a=:USER_ID_A
      and t.end_date>=sysdate