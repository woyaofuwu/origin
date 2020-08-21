UPDATE TF_B_RES_CONTRACT_TEMPPLET 
   SET 
code_type_code = :CODE_TYPE_CODE,
code_state_code = :CODE_STATE_CODE,
start_date = to_date(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'),
end_date = to_date(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),
update_time = SYSDATE,
update_staff_id = :UPDATE_STAFF_ID,
update_depart_id = :UPDATE_DEPART_ID,
remark = :REMARK,
rsrv_tag1=:RSRV_TAG1,rsrv_tag2=:RSRV_TAG2,rsrv_tag3=:RSRV_TAG3,
rsrv_date1=TO_DATE(:RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS'),
rsrv_date2=TO_DATE(:RSRV_DATE2, 'YYYY-MM-DD HH24:MI:SS'),
rsrv_date3=TO_DATE(:RSRV_DATE3, 'YYYY-MM-DD HH24:MI:SS'),
rsrv_str1=:RSRV_STR1,rsrv_str2=:RSRV_STR2,rsrv_str3=:RSRV_STR3,rsrv_str4=:RSRV_STR4,rsrv_str5=:RSRV_STR5,rsrv_str6=:RSRV_STR6,rsrv_str7=:RSRV_STR7,
rsrv_num1=:RSRV_NUM1,rsrv_num2=:RSRV_NUM2,rsrv_num3=:RSRV_NUM3
 WHERE (:CONTRACT_ID is null or contract_id = :CONTRACT_ID)
 AND (:VERSION is null or version = :VERSION)