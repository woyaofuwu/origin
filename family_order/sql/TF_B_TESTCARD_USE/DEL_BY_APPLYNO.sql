DELETE FROM tf_b_testcard_use
 WHERE apply_no=:APPLY_NO
   AND (:APPLY_BATCH_ID is null or apply_batch_id=TO_NUMBER(:APPLY_BATCH_ID))