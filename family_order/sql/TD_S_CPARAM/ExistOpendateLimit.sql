SELECT COUNT(*) RECORDCOUNT
  FROM tf_f_user a
 WHERE user_id=:USER_ID
 and add_months(open_date,1)<SYSDATE