UPDATE tf_b_testcardapply_detail
   SET code_type_code=:CODE_TYPE_CODE,start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'),end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),use_limit=TO_NUMBER(:USE_LIMIT),trade_func=:TRADE_FUNC,purpose_declare=:PURPOSE_DECLARE,pass_flag=:PASS_TAG,remark=:REMARK  
 WHERE apply_no=:APPLY_NO
   AND (:RES_NO_S is null or serial_number>=:RES_NO_S)
   AND (:RES_NO_E is null or serial_number<=:RES_NO_E)
   AND apply_batch_id=TO_NUMBER(:APPLY_BATCH_ID)
   AND (:PASS_FLAG is null or pass_flag=:PASS_FLAG)