select NULL charge_id,
       1 partition_id,
       null eparchy_code,
       null city_code,
       null cust_id,
       to_char(user_id) user_id,
       to_char(acct_id) acct_id,
       0 charge_source_code,
       0 pay_fee_mode_code,
       null recv_fee,
       0 deposit_code,
       0 cancel_tag,
       null recv_time,
       null recv_eparchy_code,
       null recv_city_code,
       null recv_depart_id,
       null recv_staff_id,
       null cancel_time,
       null cancel_eparchy_code,
       null cancel_city_code,
       null cancel_depart_id,
       null cancel_staff_id
  from (select /*+index(a,IDX_TF_A_PAYRELATION_ACCTID)*/user_id, a.acct_id
          from tf_a_payrelation a
         where a.acct_id  in(select acct_id from tf_f_account where bank_code = :BANK_CODE and bank_acct_no = :BANK_ACCT_NO)
           and start_acyc_id <=:ACYC_ID 
           and end_acyc_id >= :ACYC_ID)