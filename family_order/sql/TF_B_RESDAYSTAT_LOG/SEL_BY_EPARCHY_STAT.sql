SELECT eparchy_code,card_type_code,rsrv_tag1,to_char(sum(para_value9)) para_value9 
  FROM tf_b_resdaystat_log
 WHERE res_type_code=:RES_TYPE_CODE
   AND eparchy_code=:EPARCHY_CODE
   AND stat_type=:STAT_TYPE
   AND  oper_flag=:OPER_FLAG --库存级别
   AND (:CITY_CODE is NULL or city_code=:CITY_CODE)
   AND (:AGENT_ID is NULL or depart_id=:AGENT_ID)
   AND (:CARD_STATE_CODE IS NULL OR rsrv_tag2=:CARD_STATE_CODE)
   AND RDVALUE1 >= TO_DATE(:OPER_TIME, 'YYYY-MM-DD')
   AND RDVALUE1 <= TO_DATE(:OPER_TIME, 'YYYY-MM-DD')+1
   AND para_value1='AUTOSTAT'
   group by eparchy_code,card_type_code,rsrv_tag1