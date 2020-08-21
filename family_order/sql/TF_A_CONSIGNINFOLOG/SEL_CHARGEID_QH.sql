SELECT to_char(acct_id) acct_id,to_char(mconsign_id) mconsign_id,serial_number,to_char(user_id) user_id,to_char(bill_id) bill_id,acyc_id,recv_acyc_id,bcyc_id,can_paytag,to_char(adeb_fee) adeb_fee,to_char(aimp_fee) aimp_fee,to_char(aspay_fee) aspay_fee,to_char(vip_id) vip_id,to_char(adeblate_fee) adeblate_fee,commit_tag,to_char(charge_id) charge_id,to_char(cust_id) cust_id,city_code,pay_name,usr_name,to_char(all_money) all_money,to_char(all_new_money) all_new_money,rsrv_info1,rsrv_info2,to_char(rsrv_fee1) rsrv_fee1,to_char(rsrv_fee2) rsrv_fee2,eparchy_code,bank_acct_no,bank_code,rsrv_info3 
  FROM tf_a_consigninfolog
 WHERE mconsign_id =
 (
 select consign_id 
 from tf_a_consignlog
 where consign_id=TO_NUMBER(:CONSIGN_ID)
 and return_tag='1' 
 )
 and (charge_id!=0 or charge_id is not null)