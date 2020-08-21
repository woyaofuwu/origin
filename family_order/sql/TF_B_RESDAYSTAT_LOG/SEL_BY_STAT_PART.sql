select  card_type_code,rsrv_tag1,para_value1,para_value2,para_value3,para_value4,para_value5,para_value6,
   para_value7,para_value8,para_value9
  FROM 
  (  
      select  card_type_code,rsrv_tag1 ,
      para_value2||'0000' para_value1,
      para_value2||'9999' para_value2 ,  
      decode(rsrv_tag2 , '0', sum(para_value9), 0) para_value3,      
      decode(rsrv_tag2 , '1', sum(para_value9), 0) para_value4,     
      decode(rsrv_tag2 , '2', sum(para_value9), 0) para_value5,                   
      decode(rsrv_tag2 , '3', sum(para_value9), 0) para_value6, 
      decode(rsrv_tag2 , '4', sum(para_value9), 0) para_value7,     
      decode(rsrv_tag2 , '5', sum(para_value9), 0) para_value8, 
      decode(rsrv_tag2 , '6', sum(para_value9), 0) para_value9                     
      from tf_b_resdaystat_log
      WHERE res_type_code||''=:RES_TYPE_CODE
      AND eparchy_code=:EPARCHY_CODE
      AND stat_type=:STAT_TYPE
      AND (:OPER_FLAG is null or oper_flag=:OPER_FLAG)
      AND (:CITY_CODE is NULL or city_code=:CITY_CODE)
      AND (:AGENT_ID is null or depart_id=:AGENT_ID)
      AND (:CARD_TYPE_CODE IS NULL OR card_type_code=:CARD_TYPE_CODE)
      AND (:VALUE_CODE IS NULL OR rsrv_tag1=:VALUE_CODE)
      AND (:CARD_STATE_CODE IS NULL OR rsrv_tag2=:CARD_STATE_CODE)
      AND RDVALUE1 >= TO_DATE(:OPER_TIME, 'YYYY-MM-DD')
      AND RDVALUE1 <= TO_DATE(:OPER_TIME, 'YYYY-MM-DD')+1
      AND para_value1='AUTOSTAT'
      group by card_type_code,rsrv_tag1,rsrv_tag2,para_value2
  )