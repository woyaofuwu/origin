DELETE FROM tf_f_groupbill_user
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND acyc_id=:ACYC_ID