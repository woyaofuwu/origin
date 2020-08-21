INSERT INTO tf_f_user_plat_paraplus(partition_id,user_id,info_type,info_tag,info_code,info_value,info_name,
rsrv_str11,rsrv_str12,rsrv_str13,update_time)
SELECT MOD(user_id,10000),user_id,info_type,info_tag,info_code,info_value,info_name,
rsrv_str11,rsrv_str12,rsrv_str13,SYSDATE
  FROM tf_b_trade_plat_paraplus
 WHERE partition_id = MOD(:TRADE_ID,10000)
   AND trade_id = :TRADE_ID