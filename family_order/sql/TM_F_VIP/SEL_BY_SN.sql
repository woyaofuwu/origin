SELECT to_char(user_id) user_id,tag 
  FROM tm_f_vip
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND tag=TO_NUMBER(:TAG)