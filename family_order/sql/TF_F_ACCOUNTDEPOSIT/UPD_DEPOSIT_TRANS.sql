UPDATE tf_f_accountdeposit
   SET money=TO_NUMBER(:MONEY),start_acyc_id=:START_ACYC_ID,end_acyc_id=:END_ACYC_ID , 
      deposit_money = deposit_money + TO_NUMBER(:MONEY) - TO_NUMBER(:OLD_MONEY),
      inprint_fee =  inprint_fee + TO_NUMBER(:MONEY) - TO_NUMBER(:OLD_MONEY),
      update_time=sysdate
 WHERE
    acct_id=TO_NUMBER(:ACCT_ID)
   AND partition_id=MOD(TO_NUMBER(:ACCT_ID),10000)
   AND deposit_code=:DEPOSIT_CODE
   AND money=TO_NUMBER(:OLD_MONEY)