select distinct acct.cust_id,
                acct.acct_id,
                acct.pay_name,
                acct.bank_code,
                acct.pay_mode_code,
                acct.bank_acct_no,
                u.serial_number,
                pay.payitem_code,
                pay.start_cycle_id,
                pay.end_cycle_id,
                pay.update_time,
                pay.update_staff_id,
                u.city_code,
                u.user_id
  from tf_a_payrelation  pay,
       tf_f_account      acct,
       tf_f_user_product up,
       tf_f_user         u
 where acct.remove_tag = '0'
   and acct.partition_id = mod(pay.acct_id, 10000)
   and acct.acct_id = pay.acct_id
   and to_number(to_char(SYSDATE, 'yyyymmdd')) between pay.start_cycle_id and
       pay.end_cycle_id
   and pay.default_tag = '0'
   and pay.act_tag = '1'
   and pay.partition_id = u.partition_id
   and pay.user_id = u.user_id
   and exists (select 1
          from tf_f_cust_group grp
         where grp.cust_id = acct.cust_id
           and grp.remove_tag = '0')
   and up.partition_id = u.partition_id
   and up.user_id = u.user_id
   and up.end_date > sysdate
   and u.remove_tag = '0'
   and u.serial_number = :SERIAL_NUMBER
