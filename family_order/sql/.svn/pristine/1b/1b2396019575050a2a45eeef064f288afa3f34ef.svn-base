SELECT eparchy_code,partition_id,to_char(acct_id) acct_id,to_char(cust_id) cust_id,pay_name,city_code,pay_mode_code,bank_code,bank_acct_no,to_char(score_value) score_value,credit_class_id,to_char(basic_credit_value) basic_credit_value,to_char(credit_value) credit_value,to_char(debuty_user_id) debuty_user_id,debuty_code,contract_no,deposit_prior_rule_id,item_prior_rule_id,remove_tag,to_char(open_date,'yyyy-mm-dd hh24:mi:ss') open_date,to_char(remove_date,'yyyy-mm-dd hh24:mi:ss') remove_date,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM tf_f_account
 WHERE eparchy_code=:EPARCHY_CODE
   AND bank_code=:BANK_CODE
   AND bank_acct_no=:BANK_ACCT_NO
   AND remove_tag='0'