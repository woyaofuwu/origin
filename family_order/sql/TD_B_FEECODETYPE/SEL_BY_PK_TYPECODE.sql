--IS_CACHE=Y
SELECT eparchy_code,fee_code_rule_code,fee_code_type_code,fee_type_code,net_type_code,fee_code_rule,to_char(fee_code_fee, 0) fee_code_fee,class_id,order_serial,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3 
  FROM td_b_feecodetype
 WHERE eparchy_code=:EPARCHY_CODE
   AND fee_code_type_code=:FEE_CODE_TYPE_CODE
   AND fee_code_fee is not null
ORDER BY td_b_feecodetype.fee_code_fee desc