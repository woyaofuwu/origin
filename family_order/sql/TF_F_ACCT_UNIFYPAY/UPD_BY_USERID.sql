UPDATE tf_f_acct_unifypay
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE unify_pay_code=:UNIFY_PAY_CODE
   AND acct_id=TO_NUMBER(:ACCT_ID)
   and end_date > sysdate