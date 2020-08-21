SELECT to_char(register_id) register_id,eparchy_code,channel_id,to_char(user_id) user_id,to_char(acct_id) acct_id,bank_account_no,bank_usrp_id,pay_name,pay_address,pay_post_code,destroy_tag,inmode_code,deduct_type_code,to_char(deduct_money) deduct_money,to_char(deduct_step) deduct_step,to_char(open_time,'yyyy-mm-dd hh24:mi:ss') open_time,open_city_code,open_depart_id,open_staff_id,to_char(destroy_time,'yyyy-mm-dd hh24:mi:ss') destroy_time,destroy_city_code,destroy_depart_id,destroy_staff_id,remark 
  FROM tf_f_user_deduct
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND destroy_tag=:DESTROY_TAG