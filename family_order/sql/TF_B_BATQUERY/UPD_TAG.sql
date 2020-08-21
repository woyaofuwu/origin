UPDATE tf_b_batquery
   SET process_tag=:PROCESS_TAG  
 WHERE subscribe_id=TO_NUMBER(:SUBSCRIBE_ID)
   AND query_id=TO_NUMBER(:QUERY_ID)