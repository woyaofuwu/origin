INSERT INTO tf_b_trade_payrelation(trade_id,accept_month,user_id,acct_id,payitem_code,acct_priority,user_priority,bind_type,start_cycle_id,end_cycle_id,act_tag,default_tag,limit_type,limit,complement_tag)
SELECT TO_NUMBER(:TRADE_ID),:ACCEPT_MONTH,a.user_id,TO_NUMBER(:ACCT_ID),:PAYITEM_CODE,
:ACCT_PRIORITY,:USER_PRIORITY,:BIND_TYPE,:START_CYCLE_ID,:END_CYCLE_ID,:ACT_TAG,:DEFAULT_TAG,:LIMIT_TYPE,
TO_NUMBER(:LIMIT),:COMPLEMENT_TAG 
FROM tf_f_user a 
WHERE a.cust_id = :OLD_CUST_ID
  AND remove_tag = '0'