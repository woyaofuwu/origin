SELECT to_char(SUM(writeoff_fee)) writeoff_fee
  from (SELECT writeoff_fee
          FROM tf_a_writeofflog a
         WHERE acct_id = TO_NUMBER(:ACCT_ID)
           and acyc_id = :ACYC_ID
           AND cancel_tag = '0'
           AND a.deposit_code = :DEPOSIT_CODE
        UNION ALL
        SELECT writeoff_fee
          FROM tf_a_writeofflog_d a
         WHERE acct_id = TO_NUMBER(:ACCT_ID)
           and acyc_id = :ACYC_ID
           AND cancel_tag = '0'
           AND a.deposit_code = :DEPOSIT_CODE)