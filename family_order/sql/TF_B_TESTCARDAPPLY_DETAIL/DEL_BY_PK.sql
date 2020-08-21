DELETE FROM tf_b_testcardapply_detail
 WHERE apply_no=:APPLY_NO
   AND (:RES_NO_S is null or serial_number>=:RES_NO_S)
   AND (:RES_NO_E is null or serial_number<=:RES_NO_E)
   AND apply_batch_id=TO_NUMBER(:APPLY_BATCH_ID)
   AND (:PASS_FLAG is null or pass_flag=:PASS_FLAG)