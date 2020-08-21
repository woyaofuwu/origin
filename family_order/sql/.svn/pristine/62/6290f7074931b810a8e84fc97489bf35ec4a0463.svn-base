SELECT to_char(acct_id) acct_id,to_char(user_id) user_id,partition_id,acyc_id,detail_item_code,to_char(fee) fee,to_char(b_discnt) b_discnt,to_char(a_discnt) a_discnt,to_char(adjust_before) adjust_before,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM ts_a_detailbill_adjustbefore
 WHERE acct_id=TO_NUMBER(:ACCT_ID)
   AND user_id=TO_NUMBER(:USER_ID)
   AND partition_id=mod(:ACCT_ID,1000)
   AND acyc_id=:ACYC_ID