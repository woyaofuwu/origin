SELECT oper_date_str,res_type_code,eparchy_code,city_code,depart_id,para_value1,para_value2,para_value3,para_value4,para_value5 
  FROM tf_b_resdaystat_log
 WHERE (:OPER_DATE_STR is null or oper_date_str=:OPER_DATE_STR)
   AND res_type_code=:RES_TYPE_CODE
   AND oper_flag=:OPER_FLAG
   AND stat_type=:STAT_TYPE
   AND eparchy_code=:EPARCHY_CODE
   AND (:CITY_CODE is null or city_code=:CITY_CODE)
   AND (:DEPART_ID is null or depart_id=:DEPART_ID)