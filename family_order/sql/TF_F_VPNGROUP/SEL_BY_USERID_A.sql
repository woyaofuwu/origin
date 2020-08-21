SELECT to_char(a.user_id_a) user_id_a,to_char(a.user_id_b) user_id_b,b.serial_number rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5 
  FROM tf_f_vpngroup a,tf_f_user b
 WHERE a.user_id_a=TO_NUMBER(:USER_ID_A)
   AND a.user_id_b=b.user_id