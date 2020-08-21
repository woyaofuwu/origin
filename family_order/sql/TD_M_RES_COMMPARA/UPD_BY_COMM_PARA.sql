UPDATE /*+INDEX(a PK_TD_M_RES_COMMPARA)*/ td_m_res_commpara a
   SET para_name=:PARA_NAME,
       para_value1=:PARA_VALUE1,para_value10=TO_NUMBER(:PARA_VALUE10),
       update_time=SYSDATE,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID  
 WHERE eparchy_code=:EPARCHY_CODE
   AND para_attr=:PARA_ATTR
   AND (:PARA_CODE1 IS NULL OR para_code1=:PARA_CODE1)
   AND (:PARA_CODE2 IS NULL OR para_code2=:PARA_CODE2)