SELECT eparchy_code,to_char(acct_id) acct_id,partition_id,note_code,start_acyc_id,end_acyc_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_depart_id,update_staff_id 
  FROM tf_a_invoicetype
 WHERE acct_id=TO_NUMBER(:ACCT_ID)
   AND partition_id=MOD(TO_NUMBER(:ACCT_ID),10000)