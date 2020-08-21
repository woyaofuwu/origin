DELETE /*+INDEX(a PK_TF_M_STAFFTEMPFUNCRIGHT)*/ FROM tf_m_stafftempfuncright a 
 WHERE staff_id=:STAFF_ID
   AND (:RIGHT_CODE IS NULL OR right_code=:RIGHT_CODE) 
   AND rsvalue2 IN ('1','2')