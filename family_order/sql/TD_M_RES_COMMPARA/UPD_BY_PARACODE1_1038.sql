UPDATE td_m_res_commpara
  SET rdvalue1=SYSDATE, 
      para_value8=:PARA_VALUE8,    
      para_value9 =:PARA_VALUE9,  
      update_time=SYSDATE,
      update_staff_id=:UPDATE_STAFF_ID,
      update_depart_id=:UPDATE_DEPART_ID
  WHERE para_attr=1038
   AND para_code1=:PARA_CODE1   
   AND eparchy_code=:EPARCHY_CODE