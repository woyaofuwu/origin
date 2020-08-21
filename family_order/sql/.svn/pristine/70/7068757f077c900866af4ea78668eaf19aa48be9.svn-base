SELECT to_char(bill_id) bill_id,serial_number,to_char(user_id) user_id,to_char(acct_id) acct_id,partition_id,acyc_id,integrate_item_code,integrate_item,to_char(fee) fee,to_char(balance) balance,to_char(print_fee) print_fee,to_char(b_discnt) b_discnt,to_char(a_discnt) a_discnt,to_char(adjust_before) adjust_before,to_char(adjust_after) adjust_after,to_char(late_fee) late_fee,to_char(late_balance) late_balance,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_info1,rsrv_info2,to_char(rsrv_num1) rsrv_num1,to_char(rsrv_num2) rsrv_num2 
  FROM ts_a_subgroupbill
 WHERE acct_id=TO_NUMBER(:ACCT_ID)
  and partition_id=mod(TO_NUMBER(:ACCT_ID),10000)
   AND acyc_id=:ACYC_ID
   and  rsrv_str1=TO_NUMBER(:USER_ID)
   order by user_id ,integrate_item_code