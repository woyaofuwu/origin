select d.super_bank_code super_bank_code ,d.super_bank super_bank,
 a.bank_code bank_code ,a.bank bank,a.city_code city_code,
 to_char(count(*)) fee10 ,to_char(sum(nvl(aspay_fee,0)-nvl(aspay_late_fee,0))) aspay_fee,
 to_char(sum(decode(a.return_tag,'1',1,0))) fee11,
 to_char(sum(decode(a.return_tag,'1',a.aspay_fee-aspay_late_fee,0))) fee1,
 to_char(sum(decode(a.return_tag,'3',1,0))) fee12,
 to_char(sum(decode(a.return_tag,'3',a.aspay_fee-aspay_late_fee,0))) fee2,
 to_char(sum(decode(a.return_tag,'0',1,0))) fee13,
 to_char(sum(decode(a.return_tag,'0',a.aspay_fee-aspay_late_fee,0))) fee3,
 to_char(sum(decode(nvl(a.rsrv_number1,0),0,0,1))) fee14,
 to_char(sum(decode(nvl(a.rsrv_number1,0),0,0,a.aspay_fee-aspay_late_fee))) fee4
 from tf_a_consignlog a,
 (select b.super_bank_code super_bank_code ,b.super_bank super_bank,c.bank_code bank_code
 from td_s_superbank b,td_b_bank c
 where c.eparchy_code=:EPARCHY_CODE
 and (:BANK_CODE is null or c.bank_code=:BANK_CODE)  
 and b.super_bank_code=c.super_bank_code(+)) d
 where a.recv_acyc_id=:RECV_ACYC_ID
 and a.eparchy_code=:EPARCHY_CODE
 and a.aspay_fee >0 and a.bank_code(+) = d.bank_code 
 and (:SUPER_BANK_CODE is null or a.bank_code in (select bank_code from td_b_bank where super_bank_code=:SUPER_BANK_CODE
 and eparchy_code=:EPARCHY_CODE
))
 and (:BANK_CODE is null or a.bank_code=:BANK_CODE)
 and (:CITY_CODE is null or a.city_code like :CITY_CODE||'%' )
 and (:X_START_BANK_CODE is null or a.bank_code >=:X_START_BANK_CODE )
 and (:X_END_BANK_CODE is null or a.bank_code <=:X_END_BANK_CODE )
 group by d.super_bank_code,d.super_bank,a.bank_code,a.bank,a.city_code