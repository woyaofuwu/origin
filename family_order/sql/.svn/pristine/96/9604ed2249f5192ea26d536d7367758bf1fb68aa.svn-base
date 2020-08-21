UPDATE tf_m_stafftempdataright
   SET update_time=SYSDATE,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID  
 WHERE staff_id=:STAFF_ID
   AND data_code=:DATA_CODE
   AND (:DATA_TYPE IS NULL OR (:DATA_TYPE IS NOT NULL AND data_type=:DATA_TYPE))