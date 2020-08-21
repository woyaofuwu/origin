INSERT INTO tf_a_payrelation(partition_id, user_id, acct_id, payitem_code, acct_priority, user_priority, bind_type, start_cycle_id, end_cycle_id,inst_id,default_tag,act_tag,limit_type,limit,complement_tag,update_staff_id,update_depart_id,update_time)
SELECT MOD(a.user_id, 10000), a.user_id, a.acct_id, a.payitem_code, a.acct_priority, a.user_priority, a.bind_type, a.start_cycle_id, a.end_cycle_id, a.inst_id,a.default_tag, a.act_tag, a.limit_type, a.LIMIT, a.complement_tag, :UPDATE_STAFF_ID, :UPDATE_DEPART_ID, SYSDATE 
  FROM tf_b_trade_payrelation_bak a
 WHERE trade_id = :TRADE_ID
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND user_id = :USER_ID