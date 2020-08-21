select  t.partition_id,to_char(t.user_id) user_id,to_char(t.user_id_a) user_id_a,
t.discnt_code,t.spec_tag,t.relation_type_code,t.INST_ID,t.CAMPN_ID,
to_char(t.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(t.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,
to_char(t.UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,t.UPDATE_STAFF_ID,t.UPDATE_DEPART_ID,t.remark,
to_char(t.RSRV_NUM1) RSRV_NUM1,to_char(t.RSRV_NUM2) RSRV_NUM2,to_char(t.RSRV_NUM3) RSRV_NUM3,
to_char(t.RSRV_NUM4) RSRV_NUM4,to_char(t.RSRV_NUM5) RSRV_NUM5,t.RSRV_STR1,t.RSRV_STR2,t.RSRV_STR3,t.RSRV_STR4,
t.RSRV_STR5,to_char(t.RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,to_char(t.RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,
to_char(t.RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,t.RSRV_TAG1,t.RSRV_TAG2,t.RSRV_TAG3
 from tf_f_user_discnt t
 where    t.user_id=TO_NUMBER(:USER_ID)
      and t.partition_id=mod(TO_NUMBER(:USER_ID),10000)
      and t.user_id_a=TO_NUMBER(:USER_ID_A)
      and t.end_date>=sysdate
      and t.start_date < t.end_date
