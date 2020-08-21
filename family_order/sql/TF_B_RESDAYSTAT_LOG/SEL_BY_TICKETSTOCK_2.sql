SELECT card_type_code,to_char(sum(para_value9)) para_value9 
 FROM tf_b_resdaystat_log rl
 WHERE rl.oper_time >= TO_DATE(:OPER_TIME, 'YYYY-MM-DD')
  AND  rl.oper_time <= TO_DATE(:OPER_TIME, 'YYYY-MM-DD')+1
  AND   rl.res_type_code=:RES_TYPE_CODE 
  AND  (:RSRV_TAG1 IS NULL OR rl.rsrv_tag1=:RSRV_TAG1)
  AND  (:OPER_FLAG IS NULL OR rl.oper_flag=:OPER_FLAG)
  AND  rl.stat_type=:STAT_TYPE 
  AND  rl.eparchy_code=:EPARCHY_CODE   
  AND  (:CITY_CODE IS NULL OR rl.city_code=:CITY_CODE) 
  AND  ((:DEPART_ID_S IS NULL AND :DEPART_ID_E IS NULL) 
	  OR (rl.depart_id>=:DEPART_ID_S AND rl.depart_id<=:DEPART_ID_E))	
  AND  ((:STAFF_ID_S IS NULL AND :STAFF_ID_E IS NULL)
	  OR (rl.staff_id>=:STAFF_ID_S AND rl.staff_id<=:STAFF_ID_E))
  AND EXISTS
	(SELECT 1 FROM td_s_reskind rk
	WHERE rk.eparchy_code=:EPARCHY_CODE
	AND rk.res_type_code=:RES_TYPE_CODE
	AND rk.res_kind_code=rl.card_type_code)
 GROUP BY card_type_code