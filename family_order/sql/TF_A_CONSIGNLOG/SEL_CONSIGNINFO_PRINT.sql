select consign_id,acyc_id,bcyc_id,
 aspay_fee, aspay_late_fee,city_code,city_name, recv_time,
 pay_name,bank_acct_no,bank_code,bank,recv_bank_acct_no,recv_bank_code,
 recv_bank,recv_name,tele_count,serial_number, acct_id,
 pay_fee_mode,remark,rsrv_tag1,return_tag, rsrv_number1,rsrv_number2,
 recv_acyc_id from ( select to_char(consign_id) consign_id,acyc_id,bcyc_id,
 to_char(nvl(aspay_fee,0)-nvl(aspay_late_fee,0)) aspay_fee,to_char(nvl(aspay_late_fee,0)) aspay_late_fee,
 city_code,city_name,to_char(recv_time,'yyyy-mm-dd hh24:mi:ss') recv_time,
 pay_name,bank_acct_no,bank_code,bank,recv_bank_acct_no,recv_bank_code,
 recv_bank,recv_name,tele_count,serial_number,to_char(acct_id) acct_id,
 pay_fee_mode,remark,nvl(rsrv_tag1,'0') rsrv_tag1,nvl(return_tag,'0') return_tag,
 to_char(nvl(rsrv_number1,0)) rsrv_number1,
 to_char(nvl(rsrv_number2,0)) rsrv_number2,
 recv_acyc_id  from  tf_a_consignlog where consign_id > :CONSIGN_ID
 and recv_time >=to_date(:BEGIN_TIME ,'yyyy-mm-dd hh24:mi:ss') 
 and recv_time <=to_date(:END_TIME,'yyyy-mm-dd hh24:mi:ss') 
 and nvl(rsrv_tag2,'0')='0' and aspay_fee<>0
 and eparchy_code = :EPARCHY_CODE
 and ( :SUPER_BANK_CODE is null or bank_code in (select bank_code from td_b_bank where super_bank_code=:SUPER_BANK_CODE
 and eparchy_code = :EPARCHY_CODE ))
 and (:PAY_FEE_MODE is null or pay_fee_mode = :PAY_FEE_MODE)
 and (:CITY_CODE is null or city_code like :CITY_CODE||'%' )
 and (:X_START_BANK_CODE is null or bank_code >=:X_START_BANK_CODE )
 and (:X_END_BANK_CODE is null or bank_code <=:X_END_BANK_CODE )
 and (:X_START_BANK_ACCT_NO is null or bank_acct_no >=:X_START_BANK_ACCT_NO )
 and (:X_END_BANK_ACCT_NO is null or bank_acct_no <=:X_END_BANK_ACCT_NO )
 order by consign_id ) where  rownum <= 2000