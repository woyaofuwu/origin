SELECT DISTINCT  ACYC_ID FROM TS_A_BILL  a
WHERE ACCT_ID=TO_NUMBER(:ACCT_ID) AND PARTITION_ID=MOD(TO_NUMBER(:ACCT_ID),10000)
AND ACYC_ID >:ACYC_ID AND print_fee=0 and bill_pay_tag=1  and abs(fee+adjust_after) <> 0
and 
( exists (select 1 from tf_a_invoicetype b
  where acct_id=TO_NUMBER(:ACCT_ID) 
  AND partition_id=MOD(TO_NUMBER(:ACCT_ID),10000)
  and note_code=0 and a.acyc_id>=start_acyc_id and a.acyc_id<=end_acyc_id  ) 
  or 
  not exists (select 1 from tf_a_invoicetype where acct_id=TO_NUMBER(:ACCT_ID) 
  AND partition_id=MOD(TO_NUMBER(:ACCT_ID),10000)
  and a.acyc_id>=start_acyc_id and a.acyc_id<=end_acyc_id )
)
union all
SELECT DISTINCT  ACYC_ID FROM TS_AH_BILL  
WHERE ACCT_ID=TO_NUMBER(:ACCT_ID) AND PARTITION_ID=MOD(TO_NUMBER(:ACCT_ID),10000)
AND ACYC_ID >:ACYC_ID AND print_fee=0 and bill_pay_tag=1  and abs(fee+adjust_after) <> 0
and 
( exists (select 1 from tf_a_invoicetype 
  where acct_id=TO_NUMBER(:ACCT_ID) 
  AND partition_id=MOD(TO_NUMBER(:ACCT_ID),10000)
  and note_code=0 and acyc_id>=start_acyc_id and acyc_id<=end_acyc_id  ) 
  or 
  
  not exists (select 1 from tf_a_invoicetype where acct_id=TO_NUMBER(:ACCT_ID)
  AND partition_id=MOD(TO_NUMBER(:ACCT_ID),10000)
  and acyc_id>=start_acyc_id and acyc_id<=end_acyc_id )
)