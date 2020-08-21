UPDATE td_m_res_commpara
 SET   update_time=SYSDATE,
       update_staff_id=:UPDATE_STAFF_ID,
       update_depart_id=:UPDATE_DEPART_ID,
       para_value9=NVL(para_value9,0)+TO_NUMBER(:PARA_VALUE9)
 WHERE para_attr=:PARA_ATTR
  AND  para_code1=:PARA_CODE1