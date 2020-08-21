SELECT to_char(operate_id) operate_id,operate_type,to_char(user_id) user_id,to_char(acct_id) acct_id,acyc_id,to_char(all_return_fee) all_return_fee,to_char(bill_id) bill_id,to_char(charge_id) charge_id,status_code,enrol_staff_id,enrol_depart_id,to_char(enrol_time,'yyyy-mm-dd hh24:mi:ss') enrol_time,auditing_staff_id,auditing_depart_id,return_staff_id,return_depart_id,to_char(return_time,'yyyy-mm-dd hh24:mi:ss') return_time,bank_code,bank_acct_no,value_card_no,value_card_type_code,value_code,to_char(value_card) value_card,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,eparchy_code,to_char(auditing_time,'yyyy-mm-dd hh24:mi:ss') auditing_time 
  FROM tf_a_returnfee
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND acyc_id=:ACYC_ID
   AND operate_type <> '2'