INSERT INTO tf_f_accountdeposit_rc
  (eparchy_code,
   partition_id,
   acct_id,
   deposit_code,
   money,
   deposit_money,
   draw_money,
   inprint_fee,
   outprint_fee,
   realuse_fee1,
   realuse_fee2,
   owe_fee,
   start_acyc_id,
   end_acyc_id,
   update_time)
  SELECT eparchy_code,
         partition_id,
         acct_id,
         deposit_code,
         money,
         deposit_money,
         draw_money,
         inprint_fee,
         outprint_fee,
         realuse_fee1,
         realuse_fee2,
         owe_fee,
         start_acyc_id,
         end_acyc_id,
         update_time
    FROM tf_f_accountdeposit
   WHERE acct_id = TO_NUMBER(:ACCT_ID)
     AND partition_id = :PARTITION_ID