SELECT to_char(trade_id) trade_id,accept_month,to_char(user_id) user_id,to_char(inst_id) inst_id,to_char(acct_id) acct_id,payitem_code,acct_priority,user_priority,bind_type,start_cycle_id,end_cycle_id,default_tag,limit_type,to_char(limit) limit,complement_tag ,update_staff_id,update_depart_id
  FROM tf_b_trade_payrelation
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
and accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND user_id=TO_NUMBER(:USER_ID_A)