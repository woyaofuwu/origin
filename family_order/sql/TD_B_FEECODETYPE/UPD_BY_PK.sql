UPDATE td_b_feecodetype
   SET 
fee_code_type_code=:FEE_CODE_TYPE_CODE,
fee_type_code=:FEE_TYPE_CODE,
fee_code_rule=:FEE_CODE_RULE,
fee_code_fee=TO_NUMBER(:FEE_CODE_FEE),
class_id=:CLASS_ID,
order_serial=:ORDER_SERIAL,
update_time=SYSDATE,
update_staff_id=:UPDATE_STAFF_ID,
update_depart_id=:UPDATE_DEPART_ID,
remark=:REMARK,
rsrv_tag1=:RSRV_TAG1,rsrv_tag2=:RSRV_TAG2,rsrv_tag3=:RSRV_TAG3,
rsrv_date1=TO_DATE(:RSRV_DATE1, 'YYYY-MM-DD HH24:MI:SS'),
rsrv_date2=TO_DATE(:RSRV_DATE2, 'YYYY-MM-DD HH24:MI:SS'),
rsrv_date3=TO_DATE(:RSRV_DATE3, 'YYYY-MM-DD HH24:MI:SS'),
rsrv_str1=:RSRV_STR1,rsrv_str2=:RSRV_STR2,rsrv_str3=:RSRV_STR3  
 WHERE eparchy_code=:EPARCHY_CODE
   AND fee_code_rule_code=:FEE_CODE_RULE_CODE