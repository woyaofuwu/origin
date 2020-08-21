SELECT count(1) recordcount
  FROM ucr_crm1.tf_b_trade_discnt a
 WHERE a.trade_id = TO_NUMBER(:TRADE_ID)
   AND a.accept_month = TO_NUMBER(:ACCEPT_MONTH)
   AND a.modify_tag = '1'
   AND EXISTS (SELECT 1
       FROM td_s_commpara b
       WHERE b.subsys_code = 'CSM'
       AND b.param_attr = 355
       AND TO_NUMBER(TRIM(b.param_code)) = a.discnt_code
       AND ( (TO_NUMBER(:ACCEPT_MONTH) not in (2,3) AND a.end_date-a.start_date < TO_NUMBER(b.para_code1)) 
             OR 
             (TO_NUMBER(:ACCEPT_MONTH) in (2,3) AND a.end_date-a.start_date < TO_NUMBER(b.para_code4))  
           ) 
       AND SYSDATE < b.end_date
       AND b.eparchy_code = :EPARCHY_CODE)
   AND EXISTS (SELECT 1
       FROM ucr_crm1.tf_f_user c
       WHERE c.user_id = a.user_id
       AND c.acct_tag = '0')