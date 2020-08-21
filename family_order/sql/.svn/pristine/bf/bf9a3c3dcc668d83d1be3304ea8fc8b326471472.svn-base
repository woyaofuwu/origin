SELECT to_char(charge_id) charge_id,carrier_code,carrier_id,to_char(operate_time,'yyyy-mm-dd hh24:mi:ss') operate_time,cancel_tag,partition_id,eparchy_code,usecust_name,bank_name,bank_acct,buddy_address,contact,link_phone,purpose_declare,dealinfo,prevalue1,prevalue2,to_char(prevaluen1) prevaluen1,to_char(prevaluen2) prevaluen2,to_char(prevalued1,'yyyy-mm-dd hh24:mi:ss') prevalued1,rsrv_1 
  FROM tf_a_payother_log
 WHERE charge_id=TO_NUMBER(:CHARGE_ID) and carrier_code='2' and cancel_tag='0'
union all
SELECT to_char(charge_id) charge_id,carrier_code,carrier_id,to_char(operate_time,'yyyy-mm-dd hh24:mi:ss') operate_time,cancel_tag,partition_id,eparchy_code,usecust_name,bank_name,bank_acct,buddy_address,contact,link_phone,purpose_declare,dealinfo,prevalue1,prevalue2,to_char(prevaluen1) prevaluen1,to_char(prevaluen2) prevaluen2,to_char(prevalued1,'yyyy-mm-dd hh24:mi:ss') prevalued1,rsrv_1 
  FROM tf_a_payother_log_rc
 WHERE charge_id=TO_NUMBER(:CHARGE_ID) and carrier_code='2' and cancel_tag='0'