DELETE FROM tf_f_user_other
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND rsrv_value_code=:RSRV_VALUE_CODE
   AND rsrv_str1=TO_CHAR(:RSRV_STR1)