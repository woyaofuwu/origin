SELECT COUNT(*) recordcount
  From tf_f_user_ocs
  WHERE user_id=:USER_ID
  AND partition_id=MOD(:USER_ID,10000) AND end_date>SYSDATE