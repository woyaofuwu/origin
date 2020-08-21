SELECT sum(balance) balance
  FROM ts_a_bill
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND (partition_id,acct_id) IN (SELECT MOD(acct_id,10000),acct_id 
                                       FROM tf_a_payrelation 
                                       WHERE user_id=TO_NUMBER(:USER_ID) AND partition_id=mod(:USER_ID,10000))
   AND pay_tag = '1'
   AND integrate_item_code = '100'
   AND balance > 0