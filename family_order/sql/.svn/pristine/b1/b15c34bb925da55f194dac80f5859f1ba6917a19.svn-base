UPDATE tf_a_paylog
   SET charge_source_code=:NEWCHARGE_SOURCE_CODE,recv_time=sysdate WHERE charge_id=TO_NUMBER(:CHARGE_ID)
   AND partition_id=:PARTITION_ID
   AND charge_source_code=:OLDCHARGE_SOURCE_CODE
   AND cancel_tag=:CANCEL_TAG