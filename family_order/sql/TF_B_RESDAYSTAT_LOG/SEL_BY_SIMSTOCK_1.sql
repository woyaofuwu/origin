SELECT 
  res_type_code,oper_flag,card_type_code,para_value2,
  eparchy_code,city_code,depart_id,staff_id,rsrv_tag1,rsrv_tag2,
  to_char(para_value9) para_value9,remark2 
 FROM tf_b_resdaystat_log b
 WHERE	b.oper_time >=TO_DATE(:OPER_TIME, 'YYYY-MM-DD')
  AND   b.oper_time <=TO_DATE(:OPER_TIME, 'YYYY-MM-DD')+1
  AND   b.res_type_code=:RES_TYPE_CODE 
  AND  (:CARD_TYPE_CODE IS NULL OR b.card_type_code=:CARD_TYPE_CODE)
  AND  (:PARA_VALUE2 IS NULL OR b.para_value2=:PARA_VALUE2) 
  AND  (:OPER_FLAG IS NULL OR b.oper_flag=:OPER_FLAG)
  AND  b.stat_type=:STAT_TYPE 
  AND  b.eparchy_code=:EPARCHY_CODE   
  AND  (:CITY_CODE IS NULL OR b.city_code=:CITY_CODE) 
  AND  b.staff_id>=:STAFF_ID_S 
  AND  b.staff_id<=:STAFF_ID_E
  AND  (:PARA_VALUE9='@' OR b.para_value9>=TO_NUMBER(:PARA_VALUE9))
  AND EXISTS
      (SELECT 1 FROM td_m_res_commpara a
	 WHERE a.eparchy_code=:EPARCHY_CODE
	 AND a.para_attr=35
	 AND a.para_code1=b.para_value2
	 AND a.para_code2=:RES_TYPE_CODE)
 order by para_value2