UPDATE tf_f_user_behavior
   SET state_code=:STATE_CODE,auditing_time=TO_DATE(:AUDITING_TIME, 'YYYY-MM-DD HH24:MI:SS'),auditing_staff_id=:AUDITING_STAFF_ID,auditing_depart_id=:AUDITING_DEPART_ID,approv_info=:APPROV_INFO,remark=:REMARK  
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND in_date=TO_DATE(:IN_DATE, 'YYYY-MM-DD HH24:MI:SS')