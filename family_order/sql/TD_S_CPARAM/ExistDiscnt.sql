SELECT COUNT(*) recordcount
  FROM tf_f_user_discnt
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND discnt_code  in (1880,1883)
   and end_date>sysdate