SELECT eparchy_code,to_char(print_id) print_id,partition_id,to_char(acct_id) acct_id,to_char(user_id) user_id,serial_number,to_char(min_bill_id) min_bill_id,to_char(max_bill_id) max_bill_id,acyc_id,note_code,to_char(print_time,'yyyy-mm-dd hh24:mi:ss') print_time,print_staff_id,print_depart_id,print_city_code,print_eparchy_code,post_tag,to_char(rsrv_fee1) rsrv_fee1,to_char(rsrv_fee2) rsrv_fee2,rsrv_info1,rsrv_info2,remark 
  FROM tf_a_billprintlog
 WHERE acct_id=TO_NUMBER(:ACCT_ID)
   AND user_id=TO_NUMBER(:USER_ID)
   AND acyc_id=:ACYC_ID