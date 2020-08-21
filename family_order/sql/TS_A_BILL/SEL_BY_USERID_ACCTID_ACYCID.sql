SELECT fee fee,
       adjust_before adjust_before,
       adjust_after adjust_after,
       bill_pay_tag bill_pay_tag 
  FROM ts_a_bill
 WHERE acyc_id =:ACYC_ID
   AND user_id = :USER_ID
   AND acct_id = :ACCT_ID
UNION ALL
SELECT  fee fee,
       adjust_before adjust_before,
       adjust_after adjust_after,
       bill_pay_tag bill_pay_tag 
  FROM ts_ah_bill
 WHERE acyc_id =:ACYC_ID
   AND user_id = :USER_ID
   AND acct_id = :ACCT_ID