SELECT to_char(trade_id) trade_id,accept_month,to_char(user_id) user_id,vpmn_group_id,vpmn_group_name,max_users,discnt_code,to_char(member_user_id) member_user_id,serial_number,modify_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_b_trade_vpmnclosegrp
 WHERE trade_id=TO_NUMBER(:TRADE_ID)