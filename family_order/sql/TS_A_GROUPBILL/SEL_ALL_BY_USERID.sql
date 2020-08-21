SELECT to_char(bill_id) bill_id,to_char(user_id) user_id,to_char(acct_id) acct_id,partition_id,acyc_id,trim(bill_type)   bill_type,title,integrate_item_code,integrate_item,to_char(fee) fee,to_char(balance) balance,to_char(print_fee) print_fee,to_char(b_discnt) b_discnt,to_char(a_discnt) a_discnt,to_char(adjust_before) adjust_before,to_char(adjust_after) adjust_after,to_char(late_fee) late_fee,to_char(late_balance) late_balance,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_info1,rsrv_info2,to_char(rsrv_num1) rsrv_num1,to_char(rsrv_num2) rsrv_num2 
  FROM ts_a_groupbill
 WHERE user_id=TO_NUMBER(:USER_ID)  
   AND acyc_id=:ACYC_ID
  order by bill_type asc,title desc