DELETE FROM tf_f_fixedfee_specinfo
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND start_acyc_id > end_acyc_id