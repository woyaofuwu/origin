SELECT card_type_code,city_code,rsrv_tag1,to_char(sum(para_value9)) para_value9 
FROM tf_b_resdaystat_log
WHERE res_type_code=:RES_TYPE_CODE
AND (:EPARCHY_CODE is null or eparchy_code=:EPARCHY_CODE)
AND (:CARD_TYPE_CODE IS NULL OR card_type_code=:CARD_TYPE_CODE)
AND stat_type='0'
AND city_code=:CITY_CODE
AND oper_time >= TO_DATE(:OPER_TIME, 'YYYY-MM-DD')
AND oper_time <= TO_DATE(:OPER_TIME, 'YYYY-MM-DD')+1
group by city_code,card_type_code,rsrv_tag1