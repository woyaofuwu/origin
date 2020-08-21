SELECT  rl.res_type_code res_type_code,rl.card_type_code card_type_code,rl.para_value2 para_value2,
	rl.oper_flag,rl.eparchy_code eparchy_code,rl.city_code city_code,rl.depart_id depart_id,
	rl.staff_id staff_id,rl.rsrv_tag1 rsrv_tag1,rl.rsrv_tag2 rsrv_tag2,
        to_char(rl.para_value9) para_value9,((rl.para_value9)*(mr.value_price)/100) value_price,
	rl.remark2 remark2
 FROM tf_b_resdaystat_log rl,td_m_resvalue mr
 WHERE  rl.oper_time >=TO_DATE(:OPER_TIME, 'YYYY-MM-DD')
  AND   rl.oper_time <=TO_DATE(:OPER_TIME, 'YYYY-MM-DD')+1
  AND   rl.res_type_code=:RES_TYPE_CODE 
  AND  (:CARD_TYPE_CODE IS NULL OR rl.card_type_code=:CARD_TYPE_CODE)
  AND  (:PARA_VALUE2 IS NULL OR rl.para_value2=:PARA_VALUE2) 
  AND  (:OPER_FLAG IS NULL OR rl.oper_flag=:OPER_FLAG)
  AND  rl.stat_type=:STAT_TYPE 
  AND  rl.eparchy_code=:EPARCHY_CODE   
  AND  (:CITY_CODE IS NULL OR rl.city_code=:CITY_CODE) 
  AND  (:PARA_VALUE9='@' OR rl.para_value9>=TO_NUMBER(:PARA_VALUE9))
  AND  mr.value_code= rl.rsrv_tag1
  AND  mr.eparchy_code= rl.eparchy_code
  AND  mr.res_type_code=:RES_TYPE_CODE
  AND EXISTS
      (SELECT 1 FROM td_m_res_commpara a
       WHERE a.eparchy_code=:EPARCHY_CODE
       AND a.para_attr=35
       AND a.para_code1=rl.para_value2
       AND a.para_code2=:RES_TYPE_CODE)	
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
       AND a.depart_id=rl.depart_id 
       AND EXISTS 
           (SELECT b.depart_kind_code FROM TD_M_DEPARTKIND b
            WHERE b.code_type_code='0'
            AND b.eparchy_code=:EPARCHY_CODE
            AND b.depart_kind_code=a.depart_kind_code))
 order by card_type_code