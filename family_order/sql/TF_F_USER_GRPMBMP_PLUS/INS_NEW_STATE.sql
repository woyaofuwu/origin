INSERT INTO tf_f_user_grpmbmp_plus(partition_id,user_id,x_tag,biz_code,info_code,info_value,update_time,rsrv_str1,rsrv_str2,rsrv_str3)
SELECT MOD(user_id,10000),user_id,x_tag,biz_code,info_code,info_value,sysdate,rsrv_str1,rsrv_str2,rsrv_str3
  FROM tf_b_trade_grpmbmp_plus
 WHERE trade_id = :TRADE_ID