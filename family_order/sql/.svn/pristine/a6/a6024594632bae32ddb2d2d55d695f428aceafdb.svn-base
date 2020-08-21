SELECT a.bank_code bank_code ,a.bank bank,
 to_char(count(*)) fee10 , to_char(sum(a.aspay_fee)) aspay_fee,  
 to_char(sum(decode(a.return_tag,'1',1,0))) fee11,
 to_char(sum(decode(a.return_tag,'1',a.aspay_fee,0))) fee1,
 to_char(sum(decode(a.return_tag,'3',1,0))) fee12,
 to_char(sum(decode(a.return_tag,'3',a.aspay_fee,0))) fee2,
 to_char(sum(decode(a.return_tag,'0',1,0))) fee13,
 to_char(sum(decode(a.return_tag,'0',a.aspay_fee,0))) fee3,
 to_char(sum(decode(nvl(a.rsrv_num1,-1),-1,0,a.rsrv_num1))) fee14,
 to_char(sum(decode(nvl(a.rsrv_num1,-1),-1,0,a.aspay_fee))) fee4  
  FROM tf_a_comlot a
 WHERE recv_acyc_id=:RECV_ACYC_ID
   AND eparchy_code=:EPARCHY_CODE
   AND city_code=:CITY_CODE
   AND  a.aspay_fee>0
   AND bank_code in (select bank_code from td_b_bank where super_bank_code=:SUPER_BANK_CODE)
 group by a.bank_code,a.bank