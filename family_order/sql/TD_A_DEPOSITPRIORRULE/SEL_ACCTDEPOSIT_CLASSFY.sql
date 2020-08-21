select b.remark, SUM(MONEY) LIMIT_MONEY
  from tf_f_accountdeposit_month a, td_s_tag b
 where acct_id = :ACCT_ID
   AND partition_id = MOD(:ACCT_ID, 10000)
   and acyc_id = :ACYC_ID
   AND a.eparchy_code = b.eparchy_code
   and a.deposit_code = b.tag_number
   and b.tag_code like 'ASM_SMALLTAB_DEPOSIT%' group by b.remark