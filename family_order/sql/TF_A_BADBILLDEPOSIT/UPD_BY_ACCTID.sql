UPDATE tf_a_badbilldeposit
   SET prevaluen1=1 
 WHERE acct_id=TO_NUMBER(:ACCT_ID)