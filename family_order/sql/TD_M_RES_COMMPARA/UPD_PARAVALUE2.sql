UPDATE td_m_res_commpara
   SET para_value2=:PARA_VALUE2,para_value3=:PARA_VALUE3,remark=:REMARK,update_time=sysdate,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID  
 WHERE eparchy_code=:EPARCHY_CODE
   AND para_attr=:PARA_ATTR
   AND para_code1=:PARA_CODE1
   AND para_code2=:PARA_CODE2