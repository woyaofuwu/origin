SELECT to_char(money) money 
  FROM tf_a_badbilldeposit
 WHERE acct_id=TO_NUMBER(:ACCT_ID)
   AND prevaluen1=0