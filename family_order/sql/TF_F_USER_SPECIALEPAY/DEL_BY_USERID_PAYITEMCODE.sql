DELETE FROM tf_f_user_specialepay
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND payitem_code=:PAYITEM_CODE
   AND end_acyc_id<start_acyc_id
   AND nvl(rsrv_str1,'*')='0'