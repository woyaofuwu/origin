UPDATE td_m_staff
   SET manager_info=:MANAGER_INFO,sex=:SEX,email=:EMAIL,user_pid=:USER_PID,serial_number=:SERIAL_NUMBER,birthday=TO_DATE(:BIRTHDAY, 'YYYY-MM-DD HH24:MI:SS'),update_time=SYSDATE,
update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID  
 WHERE staff_id=:STAFF_ID
 AND dimission_tag = '0'