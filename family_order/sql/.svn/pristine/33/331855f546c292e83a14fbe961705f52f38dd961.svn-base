INSERT INTO tf_b_trade_payrelation(trade_id,accept_month,user_id,acct_id,payitem_code,acct_priority,
user_priority,bind_type,start_cycle_id,end_cycle_id,act_tag,default_tag,limit_type,limit,complement_tag)
SELECT TO_NUMBER(:TRADE_ID),:ACCEPT_MONTH,a.user_id,TO_NUMBER(:ACCT_ID),:PAYITEM_CODE,
:ACCT_PRIORITY,:USER_PRIORITY,:BIND_TYPE,:START_CYCLE_ID,:END_CYCLE_ID,:ACT_TAG,:DEFAULT_TAG,:LIMIT_TYPE,
TO_NUMBER(:LIMIT),:COMPLEMENT_TAG
FROM tf_a_payrelation a
WHERE a.acct_id = :OLD_ACCT_ID
  AND TO_CHAR(SYSDATE,'YYYYMM') BETWEEN  a.start_cycle_id AND a.end_cycle_id
  AND a.default_tag = '1'
  AND a.act_tag = '1'