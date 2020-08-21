SELECT to_char(bill_id) bill_id,to_char(acct_id) acct_id,to_char(user_id) user_id,acyc_id,to_char(fee) fee,to_char(balance) balance,to_char(late_fee) late_fee,pay_tag 
  FROM ts_a_bill_test
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND pay_tag=:PAY_TAG
ORDER BY bill_id