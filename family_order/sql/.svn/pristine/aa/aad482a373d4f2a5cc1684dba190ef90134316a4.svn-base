SELECT to_char(charge_id) charge_id,partition_id,eparchy_code,to_char(acct_id) acct_id,deposit_code,to_char(money) money,cancel_tag 
  FROM tf_a_paydepositlog
 WHERE charge_id=TO_NUMBER(:CHARGE_ID)
   AND partition_id=:PARTITION_ID
   AND cancel_tag='0'