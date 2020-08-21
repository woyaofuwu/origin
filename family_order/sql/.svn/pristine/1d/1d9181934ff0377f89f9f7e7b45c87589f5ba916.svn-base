UPDATE tf_a_writesnap_log
   SET cancel_tag = '0',RSRV_TAG1=1,REMARK='退单返销'  
 WHERE operate_id=TO_NUMBER(:OPERATE_ID)
   AND partition_id >= :PARTITION_ID - 1
   AND partition_id <= :PARTITION_ID + 1
   AND operate_type='1'
   AND cancel_tag = '1'