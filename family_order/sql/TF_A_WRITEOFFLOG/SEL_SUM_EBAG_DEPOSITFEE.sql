SELECT to_char(SUM(writeoff_fee)) writeoff_fee
  from (SELECT writeoff_fee
          FROM tf_a_writeofflog a
         WHERE acct_id = TO_NUMBER(:ACCT_ID)
           and acyc_id = :ACYC_ID
           AND cancel_tag = '0'
           AND a.deposit_code in
               (select tag_number
                  from td_s_tag b
                 where a.eparchy_code = b.eparchy_code
                   and b.tag_code like 'ASM_EBAG_DEPOSITCODE%')
        UNION ALL
        SELECT writeoff_fee
          FROM tf_a_writeofflog_d c
         WHERE acct_id = TO_NUMBER(:ACCT_ID)
           and acyc_id = :ACYC_ID
           AND cancel_tag = '0'
           AND c.deposit_code in
               (select tag_number
                  from td_s_tag d
                 where d.eparchy_code = c.eparchy_code
                   and d.tag_code like 'ASM_EBAG_DEPOSITCODE%')
        
        )