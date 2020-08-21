select eparchy_code,city_code,depart_id,card_type_code,value_code,
       sum(para_value7) para_value7,sum(para_value8) para_value8,sum(para_value9) para_value9,sum(para_value10) para_value10,
       sum(para_value11) para_value11,sum(para_value12) para_value12,sum(para_value13) para_value13,
       sum(para_value14) para_value14,sum(para_value15) para_value15,sum(para_value16) para_value16,
       sum(para_value17) para_value17,sum(para_value18) para_value18
 from tf_b_valuecard_stat_log 
 where para_value1 = :PARA_VALUE1
 AND rdvalue2 = trunc(TO_DATE(:START_DATE, 'YYYY-MM-DD'),'mm')
 AND (:CITY_CODE is null or city_code=:CITY_CODE)
 AND (:EPARCHY_CODE is null or eparchy_code=:EPARCHY_CODE)
 AND (:STOCK_ID is null or depart_id=:STOCK_ID)
 AND (:CARD_TYPE_CODE is null or card_type_code=:CARD_TYPE_CODE)
 AND (:VALUE_CODE is null or value_code=:VALUE_CODE)
 group by eparchy_code,city_code,depart_id,card_type_code,value_code