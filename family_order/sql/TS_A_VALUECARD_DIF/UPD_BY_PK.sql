UPDATE ts_a_valuecard_dif
   SET in_tag=:IN_TAG,remark=:REMARK  
 WHERE log_id=TO_NUMBER(:LOG_ID)