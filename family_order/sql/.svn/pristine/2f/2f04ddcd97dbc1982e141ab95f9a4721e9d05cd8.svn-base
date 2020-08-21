--IS_CACHE=Y
SELECT a.eparchy_code,a.city_code,a.bank_code,recv_city_code,recv_bank_code,
recv_bank_acct_no,recv_name,b.bank remark,trust_type,
to_char(a.update_time,'yyyy-mm-dd hh24:mi:ss') update_time,
a.update_staff_id,a.update_depart_id 
  FROM td_a_recv_bankaccountno a, td_b_bank b
 where a.recv_bank_code = b.bank_code