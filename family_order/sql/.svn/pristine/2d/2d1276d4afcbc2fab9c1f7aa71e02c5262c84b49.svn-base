SELECT 
  res_type_code,oper_flag,card_type_code,para_value2,
  eparchy_code,city_code,depart_id,staff_id,rsrv_tag1,rsrv_tag2,
  to_char(para_value9) para_value9,remark2 
 FROM tf_b_resdaystat_log d
 WHERE  d.oper_time >=TO_DATE(:OPER_TIME, 'YYYY-MM-DD')
  AND   d.oper_time <=TO_DATE(:OPER_TIME, 'YYYY-MM-DD')+1
  AND   d.res_type_code=:RES_TYPE_CODE 
  AND  (:CARD_TYPE_CODE IS NULL OR d.card_type_code=:CARD_TYPE_CODE)
  AND  (:PARA_VALUE2 IS NULL OR d.para_value2=:PARA_VALUE2) 
  AND  (:OPER_FLAG IS NULL OR d.oper_flag=:OPER_FLAG)
  AND  d.stat_type=:STAT_TYPE 
  AND  d.eparchy_code=:EPARCHY_CODE   
  AND  (:CITY_CODE IS NULL OR d.city_code=:CITY_CODE) 
  AND  (:PARA_VALUE9='@' OR d.para_value9>=TO_NUMBER(:PARA_VALUE9))
  AND EXISTS
      (SELECT 1 FROM td_m_res_commpara c
       WHERE c.eparchy_code=:EPARCHY_CODE
       AND c.para_attr=35
       AND c.para_code1=d.para_value2
       AND c.para_code2=:RES_TYPE_CODE)
  AND EXISTS
      (SELECT 1 FROM td_s_assignrule a
       WHERE a.eparchy_code=:EPARCHY_CODE
       AND  a.res_type_code=:RES_TYPE_CODE
       AND  a.depart_code>=:DEPART_CODE_S
       AND  a.depart_code<=:DEPART_CODE_E
       AND  a.valid_flag='0'
       AND  a.area_code=:AREA_CODE
       AND  a.start_date<=SYSDATE
       AND (a.end_date>=SYSDATE OR a.end_date IS NULL)
       AND a.depart_id=d.depart_id 
       AND EXISTS (SELECT 1 FROM TD_M_DEPARTKIND b
                   WHERE  b.code_type_code='0'
                    AND   b.eparchy_code=:EPARCHY_CODE
                    AND   b.depart_kind_code=a.depart_kind_code
               ))
 ORDER by para_value2