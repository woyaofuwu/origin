UPDATE tf_f_user_specialepay
SET end_acyc_id=:END_ACYC_ID-1,update_time=sysdate
WHERE user_id=TO_NUMBER(:USER_ID)
  AND payitem_code=:PAYITEM_CODE 
  AND end_acyc_id>=:END_ACYC_ID