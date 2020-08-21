UPDATE tf_f_user_mbmp_sub

   SET end_date=sysdate,update_time=sysdate   

 WHERE user_id=TO_NUMBER(:USER_ID)

   AND org_domain=:ORG_DOMAIN

   AND biz_state_code<>'P'
   
   AND biz_state_code<>'E'

   AND end_date>sysdate