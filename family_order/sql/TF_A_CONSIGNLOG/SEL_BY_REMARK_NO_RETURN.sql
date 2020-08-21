SELECT consign_id,pay_fee_mode,acyc_id,bcyc_id,
 bank_code,to_char(nvl(aspay_fee,0)-nvl(aspay_late_fee,0)) aspay_fee,
 to_char(nvl(aspay_late_fee,0)) aspay_late_fee,city_code,city_name,
 to_char(recv_time,'yyyy-mm-dd hh24:mi:ss') recv_time,
 pay_name,bank_acct_no,bank,recv_bank_acct_no,remark,
 recv_bank,serial_number,acct_id,tele_count, nvl(rsrv_tag1,'0') rsrv_tag1,
 recv_name,to_char(nvl(rsrv_number1,0)) rsrv_number1,nvl(return_tag,'0') return_tag ,recv_acyc_id
 FROM tf_a_consignlog
 WHERE remark = :REMARK
 AND recv_acyc_id = :RECV_ACYC_ID
 AND nvl(rsrv_tag2,'0')='0'
 AND aspay_fee<>0
 AND nvl(return_tag,'0')='0'
 AND rsrv_number2=2