UPDATE tf_f_user
SET (cust_id,usecust_id,brand_code,product_id,eparchy_code,city_code,user_passwd,user_type_code,serial_number,
score_value,credit_class,basic_credit_value,credit_value,acct_tag,prepay_tag,in_date,open_date,remove_tag,
destroy_time,pre_destroy_time,first_call_time,last_stop_time,open_mode,user_state_codeset,mpute_month_fee,
mpute_date,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,
rsrv_str10,update_time,assure_cust_id,assure_type_code,assure_date,develop_eparchy_code,develop_city_code,develop_depart_id,
develop_staff_id,develop_date,develop_no,in_depart_id,in_staff_id,remove_eparchy_code,
remove_city_code,remove_depart_id,remove_reason_code,remark ) = 
(SELECT a.cust_id,a.usecust_id,a.brand_code,a.product_id,a.eparchy_code,a.city_code,a.user_passwd,a.user_type_code,a.serial_number,a.
score_value,a.credit_class,a.basic_credit_value,a.credit_value,a.acct_tag,a.prepay_tag,a.in_date,a.open_date,a.remove_tag,a.
destroy_time,a.pre_destroy_time,a.first_call_time,a.last_stop_time,a.open_mode,a.user_state_codeset,a.mpute_month_fee,a.
mpute_date,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,a.rsrv_str6,a.rsrv_str7,a.rsrv_str8,a.rsrv_str9,a.rsrv_str10,
SYSDATE,a.assure_cust_id,a.assure_type_code,a.assure_date,a.develop_eparchy_code,a.develop_city_code,a.develop_depart_id,a.
develop_staff_id,a.develop_date,a.develop_no,a.in_depart_id,a.in_staff_id,a.remove_eparchy_code,a.
remove_city_code,a.remove_depart_id,a.remove_reason_code,a.remark FROM tf_b_trade_user_bak a WHERE a.trade_id = :TRADE_ID) 
WHERE user_id = (SELECT b.user_id FROM tf_b_trade_user_bak b WHERE b.trade_id = :TRADE_ID)