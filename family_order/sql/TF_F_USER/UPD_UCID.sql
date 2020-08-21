UPDATE tf_f_user
   SET usecust_id=TO_NUMBER(:USECUST_ID)
 WHERE user_id=TO_NUMBER(:USER_ID)