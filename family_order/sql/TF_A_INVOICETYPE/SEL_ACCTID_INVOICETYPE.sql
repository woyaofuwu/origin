SELECT to_char(acct_id) acct_id,partition_id,note_type,start_acyc_id,end_acyc_id,b.acyc_start_time,c.acyc_end_time
  FROM tf_a_invoicetype a,td_a_acycpara b,td_a_acycpara c
 WHERE acct_id=TO_NUMBER(:ACCT_ID)
   AND partition_id=:PARTITION_ID and a.start_acyc_id = b.acyc_id(+) and a.end_acyc_id = c.acyc_id(+)