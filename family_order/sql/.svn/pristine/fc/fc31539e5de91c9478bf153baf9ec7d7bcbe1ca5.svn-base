UPDATE /*+INDEX(a PK_TD_M_RES_COMMPARA)*/ td_m_res_commpara a 
 SET   para_code1=DECODE(:PARA_CODE3,NULL,para_code1,:PARA_CODE3),
       para_code2=DECODE(:PARA_CODE4,NULL,para_code2,:PARA_CODE4),              
       update_time=SYSDATE,
       update_staff_id=:UPDATE_STAFF_ID,
       update_depart_id=:UPDATE_DEPART_ID   
 WHERE para_attr=:PARA_ATTR
  AND  (:PARA_CODE1 IS NULL OR para_code1=:PARA_CODE1)  
  AND  (:PARA_CODE2 IS NULL OR para_code2=:PARA_CODE2)        
  AND  eparchy_code=:EPARCHY_CODE