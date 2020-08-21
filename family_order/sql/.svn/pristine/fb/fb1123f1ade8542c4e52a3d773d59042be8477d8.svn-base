UPDATE tf_b_resapply_detail
   SET start_res_no=:START_RES_NO,end_res_no=:END_RES_NO,res_kind_code=:RES_KIND_CODE,capacity_type_code=:CAPACITY_TYPE_CODE,apply_num=TO_NUMBER(:APPLY_NUM),audit_no=:AUDIT_NO,remind_code_a=:REMIND_CODE_A,remind_date_a =   TO_DATE(:REMIND_DATE_A,'yyyy-mm-dd hh24:mi:ss'),rsrv_str2=:RSRV_STR2,rsrv_str3=:RSRV_STR3,rsrv_str4=:RSRV_STR4,rsrv_str5=:RSRV_STR5
 WHERE apply_no=:APPLY_NO
 AND APPLY_BATCH_ID = :APPLY_BATCH_ID
  AND APPLY_DETAIL_NO = :APPLY_DETAIL_NO