update TF_F_USER_MBMP_SUB
   set end_date=sysdate ,biz_state_code='E',update_time=sysdate 
 where user_id=TO_NUMBER(:USER_ID)
   and biz_type_code=:BIZ_TYPE_CODE
   and sp_id=:SP_ID
   and sp_svc_id=:SP_SVC_ID
   and end_date>sysdate
   and biz_state_code='A'