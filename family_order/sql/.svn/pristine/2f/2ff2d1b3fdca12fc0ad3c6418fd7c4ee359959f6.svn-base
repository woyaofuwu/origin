UPDATE tf_b_resapply_main
   SET apply_date=TO_DATE(:APPLY_DATE, 'YYYY-MM-DD HH24:MI:SS'),apply_reason=:APPLY_REASON,apply_status=:APPLY_STATUS,priority=:PRIORITY,audit_type_code=:AUDIT_TYPE_CODE,audit_state_code=:AUDIT_STATE_CODE,cancel_reason=:CANCEL_REASON,remark=:REMARK,rsrv_tag1=:RSRV_TAG1,rsrv_str1=:RSRV_STR1,rsrv_num1=:RSRV_NUM1,rsrv_num2=:RSRV_NUM2  
 WHERE apply_no=:APPLY_NO
   AND apply_batch_id=TO_NUMBER(:APPLY_BATCH_ID)
   AND (:APPLY_TYPE_CODE is null or apply_type_code=:APPLY_TYPE_CODE)
   AND (:RES_TYPE_CODE is null or res_type_code=:RES_TYPE_CODE)
   AND (:APPLY_AREA_CODE is null or apply_area_code=:APPLY_AREA_CODE)
   AND (:APPLY_STAFF_ID is null or apply_staff_id=:APPLY_STAFF_ID)