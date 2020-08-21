UPDATE   td_m_res_commpara a
   SET para_value2=decode(trunc(rdvalue1),trunc(sysdate),TO_NUMBER(para_value2)+1,0),
      rdvalue1=SYSDATE,
      update_time=SYSDATE,
      update_staff_id=:UPDATE_STAFF_ID,
      update_depart_id=:UPDATE_DEPART_ID  
 WHERE eparchy_code=:EPARCHY_CODE
   AND para_attr=:PARA_ATTR
   AND (:PARA_CODE1 IS NULL OR para_code1=:PARA_CODE1)
   AND (:PARA_CODE2 IS NULL OR para_code2=:PARA_CODE2)