SELECT COUNT(1) recordcount
  FROM TF_F_USER_TRANS
  WHERE user_id=TO_NUMBER(:USER_ID)
   AND (para_code=:PARA_CODE or :PARA_CODE='*')
   AND (process_tag=:PROCESS_TAG or :PROCESS_TAG='*')
   AND sysdate< end_date