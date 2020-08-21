DELETE FROM tf_f_vpngroup
 WHERE user_id_a=TO_NUMBER(:USER_ID_A)
   AND user_id_b=TO_NUMBER(:USER_ID_B)