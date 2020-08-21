insert into tf_b_trade_detail(trade_id,accept_month,open_date,develop_date,develop_staff_id,develop_depart_id,
  remove_reason_code,develop_no,user_type_code,user_passwd,assure_cust_id,assure_type_code,
  assure_date,assure_name,assure_pspt_type_code,assure_pspt_id,assure_contact,cust_passwd,
  open_limit,pspt_type_code,pspt_id,pspt_addr,pspt_end_date,sex,birthday,nationality_code,
  local_native_code,population,language_code,folk_code,phone,post_code,post_address,fax_nbr,
  email,contact,contact_phone,home_address,work_name,work_depart,job,job_type_code,educate_degree_code,
  religion_code,revenue_level_code,marriage,character_type_code,webuser_id,web_passwd,contact_type_code,
  community_id,pay_name,pay_mode_code,bank_acct_no,bank_code,contract_no)
select to_number(:INTRADE_ID),to_number(to_char(sysdate,'mm')),sysdate,sysdate,:TRADE_STAFF_ID,:AGENT_ID,
  null,develop_no,:USER_TYPE_CODE,user_passwd,assure_cust_id,assure_type_code,
  assure_date,assure_name,assure_pspt_type_code,assure_pspt_id,assure_contact,cust_passwd,
  '0','0',:PSPT_ID,pspt_addr,pspt_end_date,sex,birthday,nationality_code,
  local_native_code,population,language_code,folk_code,phone,post_code,post_address,fax_nbr,
  email,contact,contact_phone,home_address,work_name,work_depart,job,job_type_code,educate_degree_code,
  religion_code,revenue_level_code,marriage,character_type_code,webuser_id,web_passwd,contact_type_code,
  community_id,:CUST_NAME,pay_mode_code,bank_acct_no,bank_code,contract_no
from tf_bh_trade_detail
where trade_id=to_number(:TRADE_ID)