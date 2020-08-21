SELECT *
  FROM TF_F_USER_PLATSVC a
 WHERE user_id = :USER_ID
   And partition_id = Mod(:USER_ID, 10000)
   and a.biz_type_code IN ('02', '92')
   AND a.end_date > SYSDATE
   AND a.biz_state_code IN ('A', 'N')