DELETE /*+INDEX(a PK_TF_M_STAFFTEMPDATARIGHT)*/ FROM tf_m_stafftempdataright a 
 WHERE staff_id=:STAFF_ID
   AND (:DATA_CODE IS NULL OR data_code=:DATA_CODE)
   AND (:DATA_TYPE IS NULL OR data_type=:DATA_TYPE)
   AND rsvalue2 IN ('1','2')