--IS_CACHE=Y
SELECT acyc_id, bcyc_id, to_char(acyc_start_time,'yyyy-mm-dd')
 acyc_start_time, to_char(acyc_end_time - 1, 'yyyy-mm-dd') acyc_end_time ,use_tag    
FROM td_a_acycpara
where bcyc_id<=to_number(to_char (add_months (sysdate,TO_NUMBER(:END_ACYC_ID) ),'yyyymm'))
AND   bcyc_id>= to_number(to_char(add_months(sysdate,TO_NUMBER(:START_ACYC_ID)),'yyyymm'))
order by acyc_id