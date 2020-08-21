SELECT max(to_char(operate_id)) operate_id 
  FROM tf_a_accesslog
 WHERE acct_id=TO_NUMBER(:ACCT_ID)
   AND partition_id >= :START_PARTITION_ID
   AND partition_id <= :END_PARTITION_ID
   AND cancel_tag = '0'