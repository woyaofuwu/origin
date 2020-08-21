SELECT bcyc_id,pay_name,bank_acct_no,bank,to_char(acct_id) acct_id,pay_fee_mode,refuse_reason_code,eparchy_code 
  FROM tf_a_consignlog
 WHERE bcyc_id >= :BCYC_ID
   AND serial_number=:SERIAL_NUMBER
   AND return_tag='3'