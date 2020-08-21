INSERT INTO tf_a_payother_log_rc(charge_id,carrier_code,carrier_id,operate_time,cancel_tag,partition_id,eparchy_code,usecust_name,bank_name,bank_acct,buddy_address,contact,link_phone,purpose_declare,dealinfo)
SELECT charge_id,carrier_code,carrier_id,operate_time,cancel_tag,partition_id,eparchy_code,usecust_name,bank_name,bank_acct,buddy_address,contact,link_phone,purpose_declare,dealinfo
FROM tf_a_payother_log
WHERE charge_id=TO_NUMBER(:CHARGE_ID)
   AND partition_id >= :START_PARTITION_ID
   AND partition_id <= :END_PARTITION_ID
   AND cancel_tag = '0'