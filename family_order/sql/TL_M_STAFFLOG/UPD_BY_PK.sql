UPDATE /*+INDEX(a IDX_TL_M_STAFFLOG_INTIME)*/ tl_m_stafflog a
   SET out_time=SYSDATE,remark=remark||:REMARK  
 WHERE staff_id=:STAFF_ID
   AND (:IN_TIME IS NULL OR in_time=:IN_TIME)
   AND (:IN_IP IS NULL OR in_ip=:IN_IP)
   AND out_time IS NULL