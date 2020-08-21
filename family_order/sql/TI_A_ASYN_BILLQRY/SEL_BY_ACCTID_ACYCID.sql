SELECT eparchy_code,trade_city_code,trade_depart_id,trade_staff_id,to_char(acct_id) acct_id,to_char(contract_no) contract_no,province_code,start_acyc_id,end_acyc_id,operate_type,result_code,result_info,rsrv_attr,rsrv_info,rsrv_char,to_char(rsrv_date,'yyyy-mm-dd hh24:mi:ss') rsrv_date 
  FROM ti_a_asyn_billqry
 WHERE acct_id=TO_NUMBER(:ACCT_ID)
   AND start_acyc_id=:START_ACYC_ID
   AND end_acyc_id=:END_ACYC_ID