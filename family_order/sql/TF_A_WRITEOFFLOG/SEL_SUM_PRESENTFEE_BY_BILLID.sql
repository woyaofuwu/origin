SELECT to_char(SUM(writeoff_fee)) writeoff_fee
  from (SELECT writeoff_fee
          FROM tf_a_writeofflog a
         WHERE bill_id = TO_NUMBER(:BILL_ID)
           AND cancel_tag = '0'
           AND deposit_code in
               (select tag_number
                  from td_s_tag
                 where tag_code like 'AS_ASM_DEPOSITPRESENT%'
                   AND eparchy_code = a.eparchy_code)
        UNION ALL
        SELECT writeoff_fee
          FROM tf_a_writeofflog_d a
         WHERE bill_id = TO_NUMBER(:BILL_ID)
           AND cancel_tag = '0'
           AND deposit_code in
               (select tag_number
                  from td_s_tag
                 where tag_code like 'AS_ASM_DEPOSITPRESENT%'
                   AND eparchy_code = a.eparchy_code)
        
        )