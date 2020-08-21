SELECT to_char(SUM(writeoff_fee)) writeoff_fee
  from (SELECT writeoff_fee
          FROM tf_a_writeofflog a
         WHERE bill_id = TO_NUMBER(:BILL_ID)
           AND cancel_tag = '0'
           AND a.deposit_code in
               (select tag_number
                  from td_s_tag b
                 where a.eparchy_code = b.eparchy_code
                   AND b.tag_code = 'ASM_SPECIALPRINT_DEPOSIT')
        
        UNION ALL
        SELECT writeoff_fee
          FROM tf_a_writeofflog_d a
         WHERE bill_id = TO_NUMBER(:BILL_ID)
           AND cancel_tag = '0'
           AND a.deposit_code in
               (select tag_number
                  from td_s_tag b
                 where a.eparchy_code = b.eparchy_code
                   AND b.tag_code = 'ASM_SPECIALPRINT_DEPOSIT')
        
        )