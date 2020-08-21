INSERT INTO tf_b_trade_payrelation_bak(partition_id,trade_id,accept_month,user_id,acct_id,payitem_code,acct_priority,
user_priority,bind_type,start_cycle_id,end_cycle_id,default_tag,act_tag,limit_type,limit,complement_tag,inst_id)
SELECT partition_id,TO_NUMBER(:TRADE_ID),TO_NUMBER(SUBSTR(:TRADE_ID,5,2)),user_id,acct_id,
 payitem_code,acct_priority,user_priority,bind_type,start_cycle_id,end_cycle_id,default_tag,act_tag,
 limit_type,limit,complement_tag,inst_id
  FROM tf_a_payrelation
 WHERE acct_id = :ACCT_ID
   AND end_cycle_id > TO_CHAR(SYSDATE,'YYYYMMDD')
   AND default_tag = :DEFAULT_TAG
   AND act_tag = :ACT_TAG