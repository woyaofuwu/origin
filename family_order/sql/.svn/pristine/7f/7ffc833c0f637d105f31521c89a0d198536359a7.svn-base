UPDATE tf_f_user_specialepay
SET end_acyc_id=:END_ACYC_ID-1,update_time=sysdate
WHERE user_id=TO_NUMBER(:USER_ID)
  AND end_acyc_id>=:END_ACYC_ID
  AND start_acyc_id<=end_acyc_id
  AND nvl(rsrv_str1,'*')='0'