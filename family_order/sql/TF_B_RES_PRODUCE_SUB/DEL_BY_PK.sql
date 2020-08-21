DELETE FROM tf_b_res_produce_sub
 WHERE produce_batch_id=TO_NUMBER(:PRODUCE_BATCH_ID)
   AND produce_id=:PRODUCE_ID
   AND batch_id=TO_NUMBER(:BATCH_ID)