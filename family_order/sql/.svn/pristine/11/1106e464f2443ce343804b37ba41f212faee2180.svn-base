SELECT to_char(acct_id) acct_id,to_char(user_id) user_id,partition_id,serial_number,to_char(bill_id) bill_id,acyc_id,bcyc_id,integrate_item_code,integrate_item,to_char(balance) balance,city_code,city_name,eparchy_code,eparchy_name,remove_tag,to_char(destroy_time,'yyyy-mm-dd hh24:mi:ss') destroy_time,to_char(created_date,'yyyy-mm-dd hh24:mi:ss') created_date,deal_status 
  FROM tf_a_hang_acct_deallog
 WHERE eparchy_code=:EPARCHY_CODE
   AND deal_status='0'