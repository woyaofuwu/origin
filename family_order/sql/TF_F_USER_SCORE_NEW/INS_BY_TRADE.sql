INSERT INTO tf_f_user_score_new(user_id,partition_id,brand_code,bcyc_id,score_type_code,score,score_value,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5)
select user_id,mod(user_id,10000),brand_code,bcyc_id,score_type_code,score+score_changed,score+score_changed,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5
  from tf_b_trade_score_new
 where trade_id = :TRADE_ID