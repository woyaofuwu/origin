select  sum(writeoff_fee) writeoff_fee  from 
(
SELECT writeoff_fee,deposit_code,eparchy_code FROM tf_ahb_writeofflog_2003 a
         WHERE acct_id = TO_NUMBER(:ACCT_ID) AND acyc_id = :ACYC_ID AND cancel_tag = '0'
UNION ALL
SELECT writeoff_fee,deposit_code,eparchy_code FROM tf_ahb_writeofflog_d_2003 a
         WHERE acct_id = TO_NUMBER(:ACCT_ID) AND acyc_id = :ACYC_ID AND cancel_tag = '0'
UNION ALL         
SELECT writeoff_fee,deposit_code,eparchy_code FROM tf_ahb_writeofflog_2004 a
         WHERE acct_id = TO_NUMBER(:ACCT_ID) AND acyc_id = :ACYC_ID AND cancel_tag = '0'
UNION ALL         
SELECT writeoff_fee,deposit_code,eparchy_code FROM tf_ahb_writeofflog_d_2004 a
         WHERE acct_id = TO_NUMBER(:ACCT_ID) AND acyc_id = :ACYC_ID AND cancel_tag = '0'
UNION ALL         
SELECT writeoff_fee,deposit_code,eparchy_code FROM tf_ahb_writeofflog_2005 a
         WHERE acct_id = TO_NUMBER(:ACCT_ID) AND acyc_id = :ACYC_ID AND cancel_tag = '0'
UNION ALL         
SELECT writeoff_fee,deposit_code,eparchy_code FROM tf_ahb_writeofflog_d_2005 a
         WHERE acct_id = TO_NUMBER(:ACCT_ID) AND acyc_id = :ACYC_ID AND cancel_tag = '0'
UNION ALL         
SELECT writeoff_fee,deposit_code,eparchy_code FROM tf_ahb_writeofflog_2006 a
         WHERE acct_id = TO_NUMBER(:ACCT_ID) AND acyc_id = :ACYC_ID AND cancel_tag = '0' 
UNION ALL                                                          
SELECT writeoff_fee,deposit_code,eparchy_code FROM tf_ahb_writeofflog_d_2006 a
         WHERE acct_id = TO_NUMBER(:ACCT_ID) AND acyc_id = :ACYC_ID AND cancel_tag = '0'  
UNION ALL          
SELECT writeoff_fee,deposit_code,eparchy_code FROM tf_ahb_writeofflog_2007 a
         WHERE acct_id = TO_NUMBER(:ACCT_ID) AND acyc_id = :ACYC_ID AND cancel_tag = '0'  
UNION ALL          
SELECT writeoff_fee,deposit_code,eparchy_code FROM tf_ahb_writeofflog_d_2007 a
         WHERE acct_id = TO_NUMBER(:ACCT_ID) AND acyc_id = :ACYC_ID AND cancel_tag = '0' 
UNION ALL          
SELECT writeoff_fee,deposit_code,eparchy_code FROM tf_ahb_writeofflog_2008 a
         WHERE acct_id = TO_NUMBER(:ACCT_ID) AND acyc_id = :ACYC_ID AND cancel_tag = '0'  
UNION ALL          
SELECT writeoff_fee,deposit_code,eparchy_code FROM tf_ahb_writeofflog_d_2008 a
         WHERE acct_id = TO_NUMBER(:ACCT_ID) AND acyc_id = :ACYC_ID AND cancel_tag = '0'            
         
)   a,td_a_depositpriorrule b  
 where  a.deposit_code = b.deposit_code AND a.eparchy_code = b.eparchy_code AND b.present_tag <> '0' 
AND a.deposit_code NOT IN (SELECT tag_number FROM TD_S_TAG c
WHERE a.EPARCHY_CODE=c.EPARCHY_CODE AND TAG_CODE LIKE 'ASM_NOTACT_DEPOSIT%' AND USE_TAG='1')