INSERT INTO tf_b_trade_score_new(trade_id,accept_month,user_id,serial_number,eparchy_code,city_code,brand_code,bcyc_id,score_type_code,score,score_changed,value_changed,action_code,action_count,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,remark)
SELECT trade_id,accept_month,:USER_ID_B,:SERIAL_NUMBER,:EPARCHY_CODE,:CITY_CODE,brand_code,bcyc_id,score_type_code,0,-score_changed,NULL,NULL,NULL,serial_number,NULL,NULL,NULL,NULL,NULL
  FROM tf_b_trade_score_new
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND user_id+0 = TO_NUMBER(:USER_ID_A)