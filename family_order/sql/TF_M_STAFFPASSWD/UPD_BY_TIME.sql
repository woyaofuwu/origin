UPDATE tf_m_staffpasswd
   SET update_time=SYSDATE-:UPDATE_TIME,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID  
 WHERE staff_id=:STAFF_ID