SELECT apply_id,apply_state,leader_name,leader_info,to_char(leader_date,'yyyy-mm-dd hh24:mi:ss') leader_date,prc_name,prc_phone,prc_comment,pre_approv_name,pre_approv_info,to_char(pre_approv_date,'yyyy-mm-dd hh24:mi:ss') pre_approv_date,approv_name,approv_info,to_char(approv_date,'yyyy-mm-dd hh24:mi:ss') approv_date,leader_app_name,leader_app_info,to_char(leader_app_date,'yyyy-mm-dd hh24:mi:ss') leader_app_date 
  FROM tf_b_trade_span_apply_plus
 WHERE apply_id=:APPLY_ID
   AND apply_state=:APPLY_STATE