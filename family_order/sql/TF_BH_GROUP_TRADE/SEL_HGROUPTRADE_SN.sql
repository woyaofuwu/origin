SELECT 
to_char(trade_id) trade_id,to_char(subscribe_id) subscribe_id,to_char(bpm_id) bpm_id,
trade_type_code,in_mode_code,product_id,brand_code,to_char(user_id_a) user_id_a,to_char(user_id) user_id,to_char(cust_id)cust_id,
to_char(acct_id) acct_id,(select group_id from tf_f_cust_group where cust_id = a.cust_id)group_id,serial_number_a,serial_number_b,cust_name,corp_name,short_code,to_char(accept_date,'yyyy-mm-dd hh24:mi:ss')  accept_date,
accept_month,trade_staff_id,trade_depart_id,trade_city_code,trade_eparchy_code,term_ip,eparchy_code,city_code,(select to_char(finish_date,'yyyy-mm-dd hh24:mi:ss') from tf_bh_trade where trade_id = a.trade_id) finish_date,
to_char(oper_fee) oper_fee,to_char(foregift) foregift,to_char(advance_pay) advance_pay,invoice_no,fee_state,to_char(fee_time,'yyyy-mm-dd hh24:mi:ss') fee_time,
fee_staff_id,process_tag_set,to_char(open_date,'yyyy-mm-dd hh24:mi:ss') open_date,remove_reason_code,open_limit,pspt_type_code,pspt_id,pspt_addr,
to_char(pspt_end_date,'yyyy-mm-dd hh24:mi:ss') pspt_end_date,sex,birthday,nationality_code,local_native_code,population,language_code,folk_code,phone,post_code,post_address,
fax_nbr,email,contact,contact_phone,home_address,work_name,work_depart,job,job_type_code,educate_degree_code,religion_code,revenue_level_code,marriage,character_type_code,webuser_id,
web_passwd,contact_type_code,community_id,pay_name,pay_mode_code,bank_acct_no,bank_code,contract_no,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,
rsrv_str9,rsrv_str10,remark
FROM  tf_bh_group_trade a
  WHERE  a.accept_date>=TRUNC(TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'))
     AND a.accept_date<TRUNC(TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS'))+1
       AND a.serial_number_b=:SERIAL_NUMBER
         AND a.trade_eparchy_code=:TRADE_EPARCHY_CODE