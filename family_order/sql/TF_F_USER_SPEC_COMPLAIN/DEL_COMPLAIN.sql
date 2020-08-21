DELETE FROM tf_f_user_spec_complain
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND in_date=TO_DATE(:IN_DATE, 'YYYY-MM-DD HH24:MI:SS')