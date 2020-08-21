UPDATE td_m_res_commpara
 SET   update_time=SYSDATE,
       update_staff_id=:UPDATE_STAFF_ID,
       update_depart_id=:UPDATE_DEPART_ID,
       para_value11=NVL(para_value11,0)+TO_NUMBER(:PARA_VALUE11),        
       rdvalue1=sysdate
 WHERE para_attr=:PARA_ATTR
  AND  para_code1=:PARA_CODE1