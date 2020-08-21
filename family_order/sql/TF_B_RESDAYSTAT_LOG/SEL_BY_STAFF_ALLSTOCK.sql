SELECT to_char(trade_id) trade_id,oper_date_str,to_char(oper_time,'yyyy-mm-dd hh24:mi:ss') oper_time,res_type_code,oper_flag,stat_type,oper_staff_id,card_type_code,eparchy_code,city_code,depart_id,staff_id,rsrv_tag1,rsrv_tag2,rsrv_tag3,para_value1,para_value2,para_value3,para_value4,para_value5,para_value6,para_value7,para_value8,to_char(para_value9) para_value9,to_char(para_value10) para_value10,to_char(para_value11) para_value11,to_char(para_value12) para_value12,to_char(para_value13) para_value13,to_char(para_value14) para_value14,to_char(para_value15) para_value15,to_char(para_value16) para_value16,to_char(para_value17) para_value17,to_char(para_value18) para_value18,to_char(rdvalue1,'yyyy-mm-dd hh24:mi:ss') rdvalue1,to_char(rdvalue2,'yyyy-mm-dd hh24:mi:ss') rdvalue2,remark2 
  FROM tf_b_resdaystat_log
WHERE res_type_code=:RES_TYPE_CODE
   AND (:CARD_TYPE_CODE IS NULL OR (:CARD_TYPE_CODE IS NOT NULL AND para_value2=:CARD_TYPE_CODE))
   AND depart_id||''=:DEPART_ID
   AND staff_id=:STAFF_ID
   AND (:RSRV_TAG1 IS NULL OR (:RSRV_TAG1 IS NOT NULL AND rsrv_tag1=:RSRV_TAG1))
   AND (:RSRV_TAG2 IS NULL OR (:RSRV_TAG2 IS NOT NULL AND rsrv_tag2=:RSRV_TAG2))
   AND (:PARA_VALUE1 IS NULL OR (:PARA_VALUE1 IS NOT NULL AND para_value1=:PARA_VALUE1))