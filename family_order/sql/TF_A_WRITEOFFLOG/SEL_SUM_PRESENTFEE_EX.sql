SELECT to_char(SUM(writeoff_fee)) writeoff_fee
  from (SELECT writeoff_fee
          FROM tf_a_writeofflog a, td_a_depositpriorrule b
         WHERE acct_id = TO_NUMBER(:ACCT_ID)
           and acyc_id = :ACYC_ID
           AND cancel_tag = '0'
           AND a.deposit_code = b.deposit_code
           AND a.eparchy_code=b.eparchy_code
           AND b.present_tag ='1'
        UNION ALL
        SELECT writeoff_fee
          FROM tf_a_writeofflog_d a, td_a_depositpriorrule b
         WHERE acct_id = TO_NUMBER(:ACCT_ID)
           and acyc_id = :ACYC_ID
           AND cancel_tag = '0'
           AND a.deposit_code = b.deposit_code
          AND a.eparchy_code=b.eparchy_code
           AND b.present_tag = '1'
        )