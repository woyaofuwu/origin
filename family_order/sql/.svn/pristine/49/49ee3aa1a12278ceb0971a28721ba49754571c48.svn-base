select b.PARTITION_ID, b.USER_ID, b.USER_ID_A, b.SERVICE_ID, b.MAIN_TAG, b.INST_ID, b.CAMPN_ID, b.START_DATE, b.END_DATE, b.UPDATE_TIME, b.UPDATE_STAFF_ID, b.UPDATE_DEPART_ID,
 b.REMARK, b.RSRV_NUM1, b.RSRV_NUM2, b.RSRV_NUM3, b.RSRV_NUM4, b.RSRV_NUM5, b.RSRV_STR1, b.RSRV_STR2, b.RSRV_STR3, b.RSRV_STR4, b.RSRV_STR5, b.RSRV_STR6, b.RSRV_STR7, b.RSRV_STR8, b.RSRV_STR9, b.RSRV_STR10, 
 b.RSRV_DATE1, b.RSRV_DATE2, b.RSRV_DATE3, b.RSRV_TAG1, b.RSRV_TAG2, b.RSRV_TAG3
from tf_f_user_grp_platsvc a ,tf_f_user_svc b
where a.partition_id=mod(TO_NUMBER(:USER_ID),10000) 
and a.user_id=TO_NUMBER(:USER_ID)
and a.user_id=b.user_id
and  a.service_id=b.service_id
and  sysdate between a.start_date and a.end_date
and a.biz_state_code !='N'
