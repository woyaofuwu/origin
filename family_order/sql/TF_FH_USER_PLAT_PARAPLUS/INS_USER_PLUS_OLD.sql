INSERT INTO tf_fh_user_plat_paraplus(partition_id,user_id,info_type,info_tag,info_code,info_value,info_name,
rsrv_str11,rsrv_str12,rsrv_str13,update_time,trade_id) 
SELECT partition_id,to_char(user_id) user_id,info_type,info_tag,info_code,info_value,info_name,
rsrv_str11,rsrv_str12,rsrv_str13,update_time,:TRADE_Id 
  FROM tf_f_user_plat_paraplus a
 WHERE EXISTS (SELECT 1 FROM tf_b_trade_plat_paraplus
                WHERE partition_id = a.partition_id
                  AND user_id = a.user_id
                  AND info_type = a.Info_Type
                  AND info_tag = a.Info_Tag
                  AND info_code = a.Info_Code)