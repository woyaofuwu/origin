INSERT INTO td_m_res_commpara
(eparchy_code,para_attr,para_code1,para_code2,para_name,para_value1,para_value8,para_value9,
para_value10,para_value11,rdvalue1,remark,update_time,update_staff_id,update_depart_id)
 VALUES
(:EPARCHY_CODE,:PARA_ATTR,:PARA_CODE1,:PARA_CODE2,:PARA_NAME,:PARA_VALUE1,:PARA_VALUE8,:PARA_VALUE9,
:PARA_VALUE10,:PARA_VALUE11, SYSDATE,:REMARK,SYSDATE,:UPDATE_STAFF_ID,:UPDATE_DEPART_ID)