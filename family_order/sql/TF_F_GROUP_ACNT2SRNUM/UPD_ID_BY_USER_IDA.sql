UPDATE tf_f_group_acnt2srnum
   SET user_id=TO_NUMBER(:USER_ID)  
 WHERE user_id_a=TO_NUMBER(:USER_ID_A)
   AND end_acyc_id>:END_ACYC_ID