UPDATE tf_f_user_mbmp
   SET end_date=sysdate,remark='139邮箱重复预注册' 
 WHERE user_id=TO_NUMBER(:USER_ID)
  and partition_id=mod(to_number(:USER_ID),10000)
  and biz_type_code=:BIZ_TYPE_CODE
  and biz_state_code=:BIZ_STATE_CODE
  and end_date>sysdate