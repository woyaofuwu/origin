select sum(fee) fee,sum(balance) balance,sum(late_fee) late_fee,
  sum(late_balance) late_balance,sum(adjust_before) adjust_before,
  sum(adjust_after) adjust_after,sum(a_discnt) a_discnt,
  sum(b_discnt) b_discnt,to_char(bill_id) bill_id,acyc_id  from ts_ah_bill 
  where acct_id = :ACCT_ID
  and partition_id = mod(:ACCT_ID,10000)
  and acyc_id >= :START_ACYC_ID
  and acyc_id <= :END_ACYC_ID
  group by to_char(bill_id),acyc_id