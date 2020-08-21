SELECT card_type_code,city_code,rsrv_tag1,to_char(sum(para_value9)) para_value9 
  FROM tf_b_resdaystat_log
 WHERE res_type_code=:RES_TYPE_CODE
   AND (:CARD_TYPE_CODE IS NULL OR card_type_code=:CARD_TYPE_CODE)
   AND city_code=:CITY_CODE
   AND oper_time=trunc(sysdate-1)
   group by city_code,card_type_code,rsrv_tag1