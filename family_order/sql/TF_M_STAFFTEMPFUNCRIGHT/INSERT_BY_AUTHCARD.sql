INSERT INTO tf_m_stafftempfuncright(staff_id,right_code,accredit_time,accredit_staff_id,use_tag,times,remark,rsvalue1,rsvalue2,update_time,update_staff_id,update_depart_id)
 SELECT :STAFF_ID,a.right_code,sysdate,a.staff_id,'0',1,:REMARK,'0',:RSVALUE2,sysdate,:STAFF_ID,:DEPART_ID 
  FROM td_m_tempfuncright a
 WHERE a.staff_id=:STAFF_ID_IN
   AND a.right_code=:RIGHT_CODE