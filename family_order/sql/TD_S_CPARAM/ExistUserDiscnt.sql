SELECT COUNT(*) recordcount
 FROM tf_f_user_discnt
 WHERE user_id = :USER_ID
  AND DISCNT_CODE IN (1880,1883)
  and end_date>sysdate