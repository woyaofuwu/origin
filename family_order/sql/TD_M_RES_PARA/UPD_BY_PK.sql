UPDATE TD_M_RES_PARA
   SET 
para_name=:PARA_NAME,
para_value1=:PARA_VALUE1,para_value2=:PARA_VALUE2,
para_value3=:PARA_VALUE3,para_value4=:PARA_VALUE4,
para_value5=:PARA_VALUE5,para_value6=:PARA_VALUE6,
para_value7=:PARA_VALUE7,para_value8=:PARA_VALUE8,
para_value9=TO_NUMBER(:PARA_VALUE9),para_value10=TO_NUMBER(:PARA_VALUE10),
para_value11=TO_NUMBER(:PARA_VALUE11),para_value12=TO_NUMBER(:PARA_VALUE12),
rdvalue1=TO_DATE(:RDVALUE1, 'YYYY-MM-DD HH24:MI:SS'),
rdvalue2=TO_DATE(:RDVALUE2, 'YYYY-MM-DD HH24:MI:SS'),
remark=:REMARK,
update_time=SYSDATE,
update_staff_id=:UPDATE_STAFF_ID,
update_depart_id=:UPDATE_DEPART_ID  
 WHERE eparchy_code=:EPARCHY_CODE
   AND para_attr=:PARA_ATTR
   AND para_code1=:PARA_CODE1
   AND para_code2=:PARA_CODE2