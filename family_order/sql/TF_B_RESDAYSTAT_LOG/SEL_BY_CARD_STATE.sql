SELECT to_char(trade_id) trade_id,oper_date_str,to_char(oper_time,'yyyy-mm-dd hh24:mi:ss') oper_time,res_type_code,oper_flag,stat_type,oper_staff_id,card_type_code,eparchy_code,city_code,depart_id,staff_id,rsrv_tag1,rsrv_tag2,rsrv_tag3,para_value1,para_value2,para_value3,para_value4,para_value5,para_value6,para_value7,para_value8,to_char(para_value9) para_value9,to_char(para_value10) para_value10,to_char(para_value11) para_value11,to_char(para_value12) para_value12,to_char(para_value13) para_value13,to_char(para_value14) para_value14,to_char(para_value15) para_value15,to_char(para_value16) para_value16,to_char(para_value17) para_value17,to_char(para_value18) para_value18,to_char(rdvalue1,'yyyy-mm-dd hh24:mi:ss') rdvalue1,to_char(rdvalue2,'yyyy-mm-dd hh24:mi:ss') rdvalue2,remark2 
  FROM tf_b_resdaystat_log
 WHERE oper_time>=TO_DATE(:OPER_TIME_S, 'YYYY-MM-DD HH24:MI:SS')
   AND oper_time<=TO_DATE(:OPER_TIME_E, 'YYYY-MM-DD HH24:MI:SS')
   AND (:RES_TYPE_CODE is null or res_type_code=:RES_TYPE_CODE)
   AND (:CARD_TYPE_CODE is null or card_type_code=:CARD_TYPE_CODE)
   AND (:EPARCHY_CODE is null or eparchy_code=:EPARCHY_CODE)
   AND (:CITY_CODE is null or city_code=:CITY_CODE)
   AND (:DEPART_ID is null or depart_id=:DEPART_ID)
   AND (:VALUE_CODE is null or rsrv_tag1=:VALUE_CODE)
   AND (:FACTORY_CODE is null or rsrv_tag2=:FACTORY_CODE)
   AND para_value1||''=:PARA_VALUE1