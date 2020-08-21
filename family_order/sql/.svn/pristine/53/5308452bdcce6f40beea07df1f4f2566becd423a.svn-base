SELECT eparchy_code,
       partition_id,
       to_char(acct_id) acct_id,
       deposit_code,
       to_char(money) money,
       to_char(deposit_money) deposit_money,
       to_char(draw_money) draw_money,
       to_char(inprint_fee) inprint_fee,
       to_char(outprint_fee) outprint_fee,
       0 realuse_fee1,
       0 realuse_fee2,
       0 owe_fee,
       start_acyc_id,
       end_acyc_id,
       to_char(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time
  FROM tf_f_accountdeposit_month
 WHERE acct_id = :ACCT_ID
   AND partition_id = :PARTITION_ID
   AND acyc_id = :ACYC_ID