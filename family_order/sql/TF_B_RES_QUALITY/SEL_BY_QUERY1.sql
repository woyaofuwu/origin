SELECT res_type_code,declare_area_code,bad_type_code,rsrv_str1,to_char(sum(oper_num)) oper_num,factory_code
  FROM tf_b_res_quality
 WHERE (:RES_TYPE_CODE is null or res_type_code=:RES_TYPE_CODE)
   AND time_in>=TO_DATE(:TIME_IN_S, 'YYYY-MM-DD HH24:MI:SS')
   AND time_in<=TO_DATE(:TIME_IN_E, 'YYYY-MM-DD HH24:MI:SS')
   AND (:DECLARE_AREA_CODE is null or declare_area_code=:DECLARE_AREA_CODE)
   AND (:BAD_TYPE_CODE is null or bad_type_code=:BAD_TYPE_CODE)
   AND (:RSRV_STR1 is null or rsrv_str1=:RSRV_STR1)
   AND (:FACTORY_CODE is null or factory_code=:FACTORY_CODE)
  GROUP BY declare_area_code,res_type_code,rsrv_str1,bad_type_code,factory_code