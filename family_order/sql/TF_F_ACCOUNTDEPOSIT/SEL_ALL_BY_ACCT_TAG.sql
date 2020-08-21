SELECT eparchy_code,partition_id,to_char(acct_id) acct_id,deposit_code,to_char(money) money,to_char(deposit_money) deposit_money,to_char(draw_money) draw_money,to_char(inprint_fee) inprint_fee,to_char(outprint_fee) outprint_fee,to_char(realuse_fee1) realuse_fee1,to_char(realuse_fee2) realuse_fee2,to_char(owe_fee) owe_fee,start_acyc_id,end_acyc_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM tf_f_accountdeposit
 WHERE eparchy_code=:EPARCHY_CODE
   AND partition_id=:PARTITION_ID
   AND acct_id=TO_NUMBER(:ACCT_ID)
   AND deposit_code IN ( SELECT tag_number FROM td_S_tag WHERE tag_code LIKE 'ASM_DISPLAY_DEPOSIT%' )