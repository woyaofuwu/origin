SELECT card_type_code,staff_id,rsrv_tag1,to_char(para_value9) para_value9 
  FROM tf_b_resdaystat_log
 WHERE res_type_code=:RES_TYPE_CODE
   AND (:CARD_TYPE_CODE IS NULL OR card_type_code=:CARD_TYPE_CODE)
   AND staff_id=:STAFF_ID
   AND oper_time=trunc(sysdate-1)
  order by staff_id,card_type_code,rsrv_tag1