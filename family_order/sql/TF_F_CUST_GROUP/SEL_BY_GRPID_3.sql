select  g.cust_name, a.acct_id, a.pay_name, a.pay_mode_code, u.serial_number, u.brand_code, u.score_value, u.eparchy_code, to_char(u.open_date,'yyyy-mm-dd hh24:mi:ss') open_date
 from   tf_f_cust_group g, tf_f_account a, tf_a_payrelation p, tf_f_user u
where   1 = 1
  and   g.group_id = :GROUP_ID
  and   g.cust_id = a.cust_id
  and   a.pay_mode_code = '1'   /*对公托收*/ 
  and   p.acct_id = a.acct_id
  and   p.END_CYCLE_ID > to_char(sysdate, 'yyyymmdd')  /*终止帐期大于当前日期*/ 
  and   u.user_id = p.user_id 
  and   u.partition_id = mod(p.user_id,10000)
  and   u.remove_tag = '0'
  and   a.remove_tag = '0'