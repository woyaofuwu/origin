UPDATE /*+INDEX(a PK_TD_M_RES_COMMPARA)*/ td_m_res_commpara a 
 SET para_value9=DECODE(:PARA_VALUE9,'@',NVL(para_value9,0),TO_NUMBER(:PARA_VALUE9)),
     para_value1=NVL(:PARA_VALUE1,NVL(para_value1,'0')), 
     para_value10=DECODE(:PARA_VALUE10,'@',NVL(para_value10,0),TO_NUMBER(:PARA_VALUE10)),    
     rdvalue1=DECODE(:CHECK_TAG,'1',SYSDATE,rdvalue1),
     rdvalue2=DECODE(:CHECK_TAG,'2',SYSDATE,rdvalue2),
     update_time=SYSDATE,
     update_staff_id=:UPDATE_STAFF_ID,
     update_depart_id=:UPDATE_DEPART_ID 
 WHERE para_attr=:PARA_ATTR 
   AND (:PARA_CODE1 IS NULL OR para_code1=:PARA_CODE1)
   AND (:PARA_CODE2 IS NULL OR para_code2=:PARA_CODE2)
   AND eparchy_code=:EPARCHY_CODE