select b.remark,b.deposit_code,b.deposit_name, sum(WRITEOFF_FEE) limit_money
  from 
       (select deposit_code,WRITEOFF_FEE,eparchy_code
          from tf_a_writeofflog
         where acct_id = :ACCT_ID
           AND ACYC_ID = :ACYC_ID
           AND CANCEL_TAG = '0'
        union all
        select deposit_code,WRITEOFF_FEE,eparchy_code
          from tf_a_writeofflog_d
         where acct_id = :ACCT_ID
           AND ACYC_ID = :ACYC_ID
           AND CANCEL_TAG = '0') a,td_a_depositpriorrule b
 where a.deposit_code = b.deposit_code
   and b.present_tag <> '0' and a.eparchy_code=b.eparchy_code
 GROUP BY b.remark,b.deposit_code,b.deposit_name