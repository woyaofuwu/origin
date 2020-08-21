UPDATE tf_f_user_discnt
   SET end_date= to_date(:END_DATE,'YYYY-MM-DD HH24:MI:SS'),update_time=sysdate
 WHERE user_id_a=TO_NUMBER(:USER_ID_A)
   AND discnt_code in ('3403','3404','3405','3406')
   AND end_date>SYSDATE