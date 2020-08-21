UPDATE tf_b_res_produce_sub
   SET start_res_no=:START_RES_NO,end_res_no=:END_RES_NO,res_kind_code=:RES_KIND_CODE,capacity_type_code=:CAPACITY_TYPE_CODE,assign_num=TO_NUMBER(:ASSIGN_NUM),end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),data_file=:DATA_FILE,check_file=:CHECK_FILE  
 WHERE produce_sub_id=:PRODUCE_SUB_ID
   AND produce_batch_id=TO_NUMBER(:PRODUCE_BATCH_ID)
   AND produce_id=:PRODUCE_ID
   AND batch_id=TO_NUMBER(:BATCH_ID)