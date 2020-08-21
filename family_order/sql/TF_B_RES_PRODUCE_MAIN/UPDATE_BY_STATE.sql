UPDATE tf_b_res_produce_main
   SET audit_state_code=:AUDIT_STATE_CODE,rsrv_str2=:RSRV_STR2  
 WHERE produce_id=:PRODUCE_ID
   AND batch_id=TO_NUMBER(:BATCH_ID)