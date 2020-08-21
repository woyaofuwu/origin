UPDATE tf_b_res_produce_main
   SET design_code=:DESIGN_CODE,brand_code=:BRAND_CODE,plan_end_time=TO_DATE(:PLAN_END_TIME, 'YYYY-MM-DD HH24:MI:SS'),factory_code=:FACTORY_CODE,apply_status=:APPLY_STATUS,produce_contract_id=:PRODUCE_CONTRACT_ID,produce_date=TO_DATE(:PRODUCE_DATE, 'YYYY-MM-DD HH24:MI:SS'),priority=:PRIORITY,remark=:REMARK  
 WHERE produce_id=:PRODUCE_ID
   AND batch_id=TO_NUMBER(:BATCH_ID)