SELECT to_char(SUM(writeoff_fee)) writeoff_fee
  from (SELECT writeoff_fee FROM tf_ahb_writeofflog_2003 a WHERE bill_id = TO_NUMBER(:BILL_ID)
           AND cancel_tag = '0' AND deposit_code in
               (select tag_number from td_s_tag where tag_code like 'AS_ASM_DEPOSITPRESENT%' AND eparchy_code = a.eparchy_code)
        UNION ALL
        SELECT writeoff_fee FROM tf_ahb_writeofflog_d_2003 a WHERE bill_id = TO_NUMBER(:BILL_ID)
           AND cancel_tag = '0' AND deposit_code in 
           	   (select tag_number from td_s_tag where tag_code like 'AS_ASM_DEPOSITPRESENT%' AND eparchy_code = a.eparchy_code)
        UNION ALL
        SELECT writeoff_fee FROM tf_ahb_writeofflog_2004 a WHERE bill_id = TO_NUMBER(:BILL_ID)
           AND cancel_tag = '0' AND deposit_code in
               (select tag_number from td_s_tag where tag_code like 'AS_ASM_DEPOSITPRESENT%' AND eparchy_code = a.eparchy_code)
        UNION ALL
        SELECT writeoff_fee FROM tf_ahb_writeofflog_d_2004 a WHERE bill_id = TO_NUMBER(:BILL_ID)
           AND cancel_tag = '0' AND deposit_code in 
           	   (select tag_number from td_s_tag where tag_code like 'AS_ASM_DEPOSITPRESENT%' AND eparchy_code = a.eparchy_code)
        UNION ALL
        SELECT writeoff_fee FROM tf_ahb_writeofflog_2005 a WHERE bill_id = TO_NUMBER(:BILL_ID)
           AND cancel_tag = '0' AND deposit_code in
               (select tag_number from td_s_tag where tag_code like 'AS_ASM_DEPOSITPRESENT%' AND eparchy_code = a.eparchy_code)
        UNION ALL
        SELECT writeoff_fee FROM tf_ahb_writeofflog_d_2005 a WHERE bill_id = TO_NUMBER(:BILL_ID)
           AND cancel_tag = '0' AND deposit_code in 
           	   (select tag_number from td_s_tag where tag_code like 'AS_ASM_DEPOSITPRESENT%' AND eparchy_code = a.eparchy_code)
        UNION ALL
        SELECT writeoff_fee FROM tf_ahb_writeofflog_2006 a WHERE bill_id = TO_NUMBER(:BILL_ID)
           AND cancel_tag = '0' AND deposit_code in
               (select tag_number from td_s_tag where tag_code like 'AS_ASM_DEPOSITPRESENT%' AND eparchy_code = a.eparchy_code)
        UNION ALL
        SELECT writeoff_fee FROM tf_ahb_writeofflog_d_2006 a WHERE bill_id = TO_NUMBER(:BILL_ID)
           AND cancel_tag = '0' AND deposit_code in 
           	   (select tag_number from td_s_tag where tag_code like 'AS_ASM_DEPOSITPRESENT%' AND eparchy_code = a.eparchy_code)
        UNION ALL
        SELECT writeoff_fee FROM tf_ahb_writeofflog_2007 a WHERE bill_id = TO_NUMBER(:BILL_ID)
           AND cancel_tag = '0' AND deposit_code in
               (select tag_number from td_s_tag where tag_code like 'AS_ASM_DEPOSITPRESENT%' AND eparchy_code = a.eparchy_code)
        UNION ALL
        SELECT writeoff_fee FROM tf_ahb_writeofflog_d_2007 a WHERE bill_id = TO_NUMBER(:BILL_ID)
           AND cancel_tag = '0' AND deposit_code in 
           	   (select tag_number from td_s_tag where tag_code like 'AS_ASM_DEPOSITPRESENT%' AND eparchy_code = a.eparchy_code)
        UNION ALL
        SELECT writeoff_fee FROM tf_ahb_writeofflog_2008 a WHERE bill_id = TO_NUMBER(:BILL_ID)
           AND cancel_tag = '0' AND deposit_code in
               (select tag_number from td_s_tag where tag_code like 'AS_ASM_DEPOSITPRESENT%' AND eparchy_code = a.eparchy_code)
        UNION ALL
        SELECT writeoff_fee FROM tf_ahb_writeofflog_d_2008 a WHERE bill_id = TO_NUMBER(:BILL_ID)
           AND cancel_tag = '0' AND deposit_code in 
           	   (select tag_number from td_s_tag where tag_code like 'AS_ASM_DEPOSITPRESENT%' AND eparchy_code = a.eparchy_code)
        
        )