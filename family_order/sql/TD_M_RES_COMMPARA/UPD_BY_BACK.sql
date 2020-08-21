UPDATE td_m_res_commpara
   SET para_value2=:PARA_VALUE2,para_value5=to_char(sysdate+:DATE,'YYYY-MM-DD HH24:MI:SS'),
para_value3=to_char(sysdate,'YYYY-MM-DD HH24:MI:SS'),para_value9=para_value9+1,
update_time=SYSDATE,update_staff_id=:UPDATE_STAFF_ID,
update_depart_id=:UPDATE_DEPART_ID,remark=:REMARK  
 WHERE eparchy_code=:EPARCHY_CODE
   AND para_attr=:PARA_ATTR
   AND para_code1=:PARA_CODE1
   AND (:PARA_CODE2 is null or para_code2=:PARA_CODE2) 
   AND (:PARA_VALUE21 is null or para_value2=:PARA_VALUE21)
   AND (:SERIAL_NUMBER is null or para_value7=:SERIAL_NUMBER)