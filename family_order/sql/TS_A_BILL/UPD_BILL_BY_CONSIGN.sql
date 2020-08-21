UPDATE ts_a_bill
   SET  pay_tag=:PAY_TAG 
 WHERE 
   user_id=TO_NUMBER(:USER_ID)
   AND partition_id=mod(to_number(:ACCT_ID),10000)
   AND bill_id=TO_NUMBER(:BILL_ID)
   AND acyc_id=:ACYC_ID