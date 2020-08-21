SELECT to_char(SUM(writeoff_fee)) writeoff_fee
  from (SELECT writeoff_fee
          FROM tf_a_writeofflog a, td_a_depositpriorrule b
         WHERE acct_id = TO_NUMBER(:ACCT_ID)
           and user_id = TO_NUMBER(:USER_ID)
           and acyc_id = :ACYC_ID
           AND cancel_tag = '0'
           AND a.deposit_code = b.deposit_code
           AND a.eparchy_code=b.eparchy_code
           AND b.present_tag <> '0' AND a.deposit_code NOT IN (SELECT  tag_number FROM TD_S_TAG c
WHERE a.EPARCHY_CODE=c.EPARCHY_CODE AND TAG_CODE like 'ASM_NOTACT_DEPOSIT%' AND USE_TAG='1')
        UNION ALL
        SELECT writeoff_fee
          FROM tf_a_writeofflog_d a, td_a_depositpriorrule b
         WHERE acct_id = TO_NUMBER(:ACCT_ID)
           and  user_id = TO_NUMBER(:USER_ID)
           and acyc_id = :ACYC_ID
           AND cancel_tag = '0'
           AND a.deposit_code = b.deposit_code
          AND a.eparchy_code=b.eparchy_code
           AND b.present_tag <> '0'AND a.deposit_code NOT IN (SELECT  tag_number FROM TD_S_TAG c
WHERE a.EPARCHY_CODE=c.EPARCHY_CODE AND TAG_CODE like 'ASM_NOTACT_DEPOSIT%' AND USE_TAG='1')
        
        )