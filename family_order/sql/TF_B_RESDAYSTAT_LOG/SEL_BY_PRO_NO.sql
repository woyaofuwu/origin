SELECT eparchy_code,para_value2 para_value2,sum(para_value9) para_value9 FROM tf_b_resdaystat_log
WHERE res_type_code=:RES_TYPE_CODE
AND stat_type='Z'
AND (:CITY_CODE is null or city_code=:CITY_CODE)
AND (:SIM_CARD_NO is null or para_value2=:SIM_CARD_NO)
AND eparchy_code=:EPARCHY_CODE
AND oper_time >= TO_DATE(:OPER_TIME, 'YYYY-MM-DD')
AND oper_time <= TO_DATE(:OPER_TIME, 'YYYY-MM-DD')+1
group by eparchy_code,para_value2