UPDATE tf_b_res_produce_main
   SET audit_type_code=:AUDIT_TYPE_CODE,plan_end_time=TO_DATE(:PLAN_START_TIME, 'YYYY-MM-DD HH24:MI:SS'),assign_num=TO_NUMBER(:ASSIGN_NUM),factory_code=:FACTORY_CODE,rsrv_tag1=:RSRV_TAG1,rsrv_tag2=:RSRV_TAG2,rsrv_date2=TO_DATE(:PLAN_END_TIME, 'YYYY-MM-DD HH24:MI:SS'),rsrv_str3=:RSRV_STR3  
 WHERE produce_id=:PRODUCE_ID
   AND batch_id=TO_NUMBER(:BATCH_ID)