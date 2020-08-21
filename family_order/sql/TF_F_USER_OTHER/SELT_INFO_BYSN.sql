select to_char(a.user_id) user_id, a.product_id product_id, to_char(b.acct_id) acct_id from tf_f_user a, tf_a_payrelation b 
where a.serial_number = :SERIAL_NUMBER and a.remove_tag = '0' and b.user_id = a.user_id 
and b.payitem_code = '-1' and b.default_tag = '1' and act_tag = '1' and end_acyc_id >(
select acyc_id from td_a_acycpara where acyc_start_time <= sysdate and acyc_end_time >sysdate
)