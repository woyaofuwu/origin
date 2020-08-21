UPDATE tf_f_user_mbmp
   SET biz_state_code='E',end_date=sysdate,remark='用户注销' 
 WHERE user_id=:USER_ID and partition_id=mod(:USER_ID,10000)
  and biz_type_code=:BIZ_TYPE_CODE
  and end_date>sysdate