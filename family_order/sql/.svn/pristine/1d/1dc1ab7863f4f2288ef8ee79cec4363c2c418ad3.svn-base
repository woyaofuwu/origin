--IS_CACHE=Y
SELECT eparchy_code,city_code,bank_code,recv_city_code,recv_bank_code,recv_bank_acct_no,recv_name,remark,trust_type,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
  FROM td_a_recv_bankaccountno
 WHERE eparchy_code=:EPARCHY_CODE
   AND bank_code=:BANK_CODE