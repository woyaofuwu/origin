select card_type_code,Rsrv_Tag1,sum(para_value1) para_value1,sum(TO_NUMBER(para_value2)/100)  para_value2
 from tf_b_resdaystat_log 
where eparchy_code=:EPARCHY_CODE 
 and res_type_code='3'
 and (:STOCK_ID is null or depart_id=:STOCK_ID)
 and (:CITY_CODE is null or city_code=:CITY_CODE)
 and oper_time >= TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')
 and oper_time <=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')
 group by card_type_code,Rsrv_Tag1