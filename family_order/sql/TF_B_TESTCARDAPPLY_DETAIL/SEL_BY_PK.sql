SELECT apply_no,serial_number,to_char(apply_batch_id) apply_batch_id,code_type_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,area_code,apply_area_code,to_char(use_limit) use_limit,trade_func,purpose_declare,pass_flag,remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_num1,rsrv_num2,rsrv_num3,0 x_tag 
  FROM tf_b_testcardapply_detail
 WHERE apply_no=:APPLY_NO
   AND (:RES_NO_S is null or serial_number>=:RES_NO_S)
   AND (:RES_NO_E is null or serial_number<=:RES_NO_E)
   AND (:APPLY_BATCH_ID is null or apply_batch_id=TO_NUMBER(:APPLY_BATCH_ID))
   AND (:PASS_FLAG is null or pass_flag=:PASS_FLAG)