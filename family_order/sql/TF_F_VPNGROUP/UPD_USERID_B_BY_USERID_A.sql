UPDATE tf_f_vpngroup
   SET user_id_b=TO_NUMBER(:USER_ID_B)  
 WHERE user_id_a=TO_NUMBER(:USER_ID_A)