SELECT to_char(user_id) user_id,acyc_id,integrate_item_code,to_char(bill_id) bill_id,to_char(cash_return_fee) cash_return_fee,to_char(adjust_return_fee) adjust_return_fee,to_char(cash_encourage_fee) cash_encourage_fee,to_char(adjust_encourage_fee) adjust_encourage_fee,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,eparchy_code 
  FROM tf_a_sumreturnfee
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND acyc_id=:ACYC_ID
   AND eparchy_code=:EPARCHY_CODE