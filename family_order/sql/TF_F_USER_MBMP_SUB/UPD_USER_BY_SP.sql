update TF_F_USER_MBMP_SUB
   set end_date=sysdate ,remark = :REMARK,update_time=sysdate 
   where user_id=TO_NUMBER(:USER_ID)
   AND partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   and biz_type_code=:BIZ_TYPE_CODE
   and sp_id=:SP_ID
   and sp_svc_id=:SP_SVC_ID
   and biz_state_code=:BIZ_STATE_CODE
   and sysdate BETWEEN start_date AND end_date