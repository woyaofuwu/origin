UPDATE tf_f_user_mbmp_sub           

   SET end_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'),update_time=sysdate          

 WHERE user_id=TO_NUMBER(:USER_ID) 

   AND sp_id=:SP_ID    

   AND biz_type_code=:BIZ_TYPE_CODE

   AND org_domain=:ORG_DOMAIN      

   AND sp_svc_id=:SP_SVC_ID

   AND end_date>sysdate