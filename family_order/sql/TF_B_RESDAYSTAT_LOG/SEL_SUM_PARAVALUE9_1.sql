SELECT NVL(to_char(sum(para_value9)),'0') para_value9    
 FROM tf_b_resdaystat_log    
 WHERE oper_time=TRUNC(SYSDATE -1)
  AND  res_type_code=:RES_TYPE_CODE 
  AND  (:CARD_TYPE_CODE IS NULL OR card_type_code=:CARD_TYPE_CODE)
  AND  (:RSRV_TAG1 IS NULL OR rsrv_tag1=:RSRV_TAG1)
  AND  (:OPER_FLAG IS NULL OR oper_flag=:OPER_FLAG)
  AND  stat_type=:STAT_TYPE 
  AND  eparchy_code=:EPARCHY_CODE   
  AND  (:CITY_CODE IS NULL OR city_code=:CITY_CODE) 
  AND  (:DEPART_ID IS NULL OR depart_id=:DEPART_ID)
  AND  (:STAFF_ID IS NULL OR staff_id=:STAFF_ID)