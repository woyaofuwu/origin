UPDATE td_m_res_commpara
   SET para_value2=:PARA_VALUE2, para_value7=:SERIAL_NUMBER,para_value5=NULL,
update_time=SYSDATE,update_staff_id=:UPDATE_STAFF_ID,
update_depart_id=:UPDATE_DEPART_ID,remark=:REMARK  
 WHERE eparchy_code=:EPARCHY_CODE
   AND para_attr=:PARA_ATTR
   AND para_code1=:PARA_CODE1
   AND para_code2=:PARA_CODE2
   AND para_value2=:PARA_VALUE21 
   AND (:PARA_VALUE5='@'  or (para_value5 IS NULL OR (para_value5 is not null
   and para_value5 < to_char(sysdate,'YYYY-MM-DD HH24:MI:SS')) ) )