SELECT card_type_code,to_char(sum(para_value9)) para_value9 
 FROM tf_b_resdaystat_log rl
 WHERE	rl.oper_time >=TO_DATE(:OPER_TIME, 'YYYY-MM-DD')
  AND  rl.oper_time <=TO_DATE(:OPER_TIME, 'YYYY-MM-DD')+1
  AND   rl.res_type_code=:RES_TYPE_CODE 
  AND  (:RSRV_TAG1 IS NULL OR rl.rsrv_tag1=:RSRV_TAG1)
  AND  (:OPER_FLAG IS NULL OR rl.oper_flag=:OPER_FLAG)
  AND  rl.stat_type=:STAT_TYPE 
  AND  rl.eparchy_code=:EPARCHY_CODE   
  AND  (:CITY_CODE IS NULL OR rl.city_code=:CITY_CODE) 
  AND EXISTS
 (SELECT 1 FROM td_s_reskind rk
   WHERE rk.eparchy_code=:EPARCHY_CODE
   AND  rk.res_type_code=:RES_TYPE_CODE
   AND  rk.res_kind_code=rl.card_type_code)
  AND EXISTS
 (SELECT 1 FROM td_s_assignrule a
   WHERE a.eparchy_code=:EPARCHY_CODE
   AND  a.res_type_code='1'--:RES_TYPE_CODE
   AND  a.depart_code>=:DEPART_CODE_S
   AND  a.depart_code<=:DEPART_CODE_E
   AND  a.valid_flag=:VALID_FLAG
   AND  a.area_code=:AREA_CODE
   AND  a.start_date<=SYSDATE
   AND (a.end_date>=SYSDATE OR a.end_date IS NULL)
   AND a.depart_id=rl.depart_id 
   AND EXISTS (SELECT b.depart_kind_code
                 FROM TD_M_DEPARTKIND b
                WHERE b.code_type_code='0'
                  AND b.eparchy_code=:EPARCHY_CODE
                  AND b.depart_kind_code=a.depart_kind_code
               ))
 GROUP BY card_type_code