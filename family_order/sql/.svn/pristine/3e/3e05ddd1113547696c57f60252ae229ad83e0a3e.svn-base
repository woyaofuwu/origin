SELECT NVL(to_char(sum(para_value9)),'0') para_value9    
 FROM tf_b_resdaystat_log    
 WHERE oper_date_str>=:START_DATE
  AND  oper_date_str<=:END_DATE 
  AND  res_type_code=:RES_TYPE_CODE 
  AND  oper_flag=:OPER_FLAG 
  AND  stat_type=:STAT_TYPE 
  AND  eparchy_code=:EPARCHY_CODE   
  AND  (:CITY_CODE IS NULL OR city_code=:CITY_CODE) 
  AND  (:DEPART_ID IS NULL OR depart_id=:DEPART_ID)