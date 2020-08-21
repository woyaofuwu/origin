SELECT card_type_code,eparchy_code,rsrv_tag1,to_char(sum(para_value9)) para_value9 
FROM tf_b_resdaystat_log
WHERE res_type_code=:RES_TYPE_CODE
AND (:CITY_CODE is null or city_code=:CITY_CODE)
AND (:CARD_TYPE_CODE IS NULL OR card_type_code=:CARD_TYPE_CODE)
AND eparchy_code=:EPARCHY_CODE
AND stat_type='0'
AND oper_time >= TO_DATE(:OPER_TIME, 'YYYY-MM-DD')
AND oper_time <= TO_DATE(:OPER_TIME, 'YYYY-MM-DD')+1
group by eparchy_code,card_type_code,rsrv_tag1