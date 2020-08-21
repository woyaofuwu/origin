INSERT INTO tf_m_stafftempdataright(staff_id,data_code,data_type,accredit_time,accredit_staff_id,use_tag,times,remark,rsvalue1,rsvalue2,update_time,update_staff_id,update_depart_id)
 SELECT :STAFF_ID,a.data_code,a.data_type,sysdate,a.staff_id,'0',1,:REMARK,'0',:RSVALUE2,SYSDATE,:STAFF_ID,:DEPART_ID   
  FROM td_m_tempdataright a
 WHERE a.staff_id=:STAFF_ID_IN
   AND a.data_code=:DATA_CODE