SELECT 
  res_type_code,oper_flag,stat_type,card_type_code,
  eparchy_code,city_code,depart_id,staff_id,rsrv_tag1,
  to_char(para_value9) para_value9,remark2 
 FROM tf_b_resdaystat_log
 WHERE oper_time>=TO_DATE(:OPER_TIME, 'YYYY-MM-DD')
  AND  oper_time<=TO_DATE(:OPER_TIME, 'YYYY-MM-DD')+1
  AND   res_type_code=:RES_TYPE_CODE 
  AND  (:CARD_TYPE_CODE IS NULL OR card_type_code=:CARD_TYPE_CODE)
  AND  (:RSRV_TAG1 IS NULL OR rsrv_tag1=:RSRV_TAG1)
  AND  (:OPER_FLAG IS NULL OR oper_flag=:OPER_FLAG)
  AND  stat_type=:STAT_TYPE 
  AND  eparchy_code=:EPARCHY_CODE   
  AND  (:CITY_CODE IS NULL OR city_code=:CITY_CODE) 
  AND  ((:DEPART_ID_S IS NULL AND :DEPART_ID_E IS NULL) 
	  OR (depart_id>=:DEPART_ID_S AND depart_id<=:DEPART_ID_E))	
  AND  ((:STAFF_ID_S IS NULL AND :STAFF_ID_E IS NULL)
	  OR (staff_id>=:STAFF_ID_S AND staff_id<=:STAFF_ID_E))
  AND  (:PARA_VALUE9='@' OR para_value9>=TO_NUMBER(:PARA_VALUE9))