SELECT to_char(trade_id) trade_id,accept_month,to_char(user_id) user_id,serial_number,eparchy_code,city_code,brand_code,bcyc_id,score_type_code,to_char(score) score,to_char(score_changed) score_changed,to_char(value_changed) value_changed,action_code,to_char(action_count) action_count,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,remark 
  FROM tf_b_trade_score_new
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND user_id = TO_NUMBER(:USER_ID)
ORDER BY brand_code,bcyc_id,score_type_code