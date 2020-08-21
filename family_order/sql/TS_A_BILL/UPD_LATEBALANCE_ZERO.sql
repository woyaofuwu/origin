UPDATE ts_a_bill
   SET late_fee = 0, late_balance = 0, latecal_date = NULL
 WHERE acct_id = TO_NUMBER(:ACCT_ID)
   AND partition_id = mod(TO_NUMBER(:ACCT_ID), 10000)
   AND late_balance > 0
   AND bill_pay_tag = '0'