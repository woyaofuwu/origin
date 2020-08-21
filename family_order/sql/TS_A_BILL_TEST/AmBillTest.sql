SELECT to_char(bill_id) bill_id,to_char(acct_id) acct_id,to_char(user_id) user_id,partition_id,acyc_id,integrate_item_code,to_char(fee) fee,to_char(balance) balance,to_char(late_fee) late_fee,to_char(late_balance) late_balance,canpay_tag,pay_tag 
  FROM ts_a_bill_test
WHERE user_id = TO_NUMBER(:USER_ID) AND acyc_id>=:ACYC_ID_S AND
       acyc_id<=:ACYC_ID_E  AND pay_tag= 0