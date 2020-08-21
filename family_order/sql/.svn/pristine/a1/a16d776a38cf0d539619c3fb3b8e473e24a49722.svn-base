INSERT INTO td_m_res_commpara
(eparchy_code,para_attr,para_code1,para_code2,para_name,para_value1,para_value2,para_value3,
para_value4,para_value5,para_value6,para_value7,para_value8,para_value9,para_value10,para_value11,
para_value12,rdvalue1,rdvalue2,remark,update_time,update_staff_id,update_depart_id)          
 SELECT eparchy_code,1044,para_code1,:PARA_CODE2,para_name,para_value1,para_value2,para_value3,
 para_value4,para_value5,para_value6,para_value7,para_value8,para_value9,para_value10,para_value11,
 para_value12,rdvalue1,rdvalue2,remark,SYSDATE,:UPDATE_STAFF_ID,:UPDATE_DEPART_ID
 FROM td_m_res_commpara
 WHERE para_attr=1044
  AND  para_code1=:PARA_CODE1        
  AND  para_code2='$$$$$'         
  AND  eparchy_code=:EPARCHY_CODE