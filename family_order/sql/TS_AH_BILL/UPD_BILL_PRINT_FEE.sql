UPDATE ts_ah_bill
   SET print_fee=fee+adjust_after+late_fee
 WHERE acct_id=TO_NUMBER(:ACCT_ID)
   AND partition_id=MOD(TO_NUMBER(:ACCT_ID),10000)
   AND acyc_id>=:START_ACYC_ID
   AND acyc_id<=:END_ACYC_ID
   AND print_fee=0
   AND bill_pay_tag='1'