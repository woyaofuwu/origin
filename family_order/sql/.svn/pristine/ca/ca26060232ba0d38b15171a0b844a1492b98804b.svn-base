UPDATE tf_a_accesslog
   SET cancel_tag = '1'  
 WHERE operate_id=TO_NUMBER(:OPERATE_ID)
   AND operate_type=:OPERATE_TYPE
   AND partition_id = :PARTITION_ID
   AND cancel_tag = '0'