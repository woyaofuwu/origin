SELECT to_char(consign_id) consign_id,to_char(charge_id) charge_id,acyc_id,bcyc_id,to_char(nvl(aspay_fee,0)) aspay_fee,to_char(nvl(aspay_late_fee,0)) aspay_late_fee,city_code,city_name,to_char(recv_time,'yyyy-mm-dd hh24:mi:ss') recv_time,pay_name,bank_acct_no,bank_code,bank,recv_bank_acct_no,recv_bank_code,recv_bank,recv_name,tele_count,serial_number,to_char(acct_id) acct_id,pay_fee_mode,to_char(vip_id) vip_id,remark,nvl(rsrv_tag1,0) rsrv_tag1,nvl(rsrv_tag2,'0') rsrv_tag2,nvl(return_tag,'0') return_tag,to_char(return_time,'yyyy-mm-dd hh24:mi:ss') return_time,refuse_reason_code,to_char(nvl(rsrv_number2,0)) rsrv_number2,to_char(nvl(rsrv_number1,0)) rsrv_number1,recv_acyc_id 
  FROM tf_a_consignlog
 WHERE remark=:REMARK
   AND acyc_id=:ACYC_ID
   AND nvl(return_tag,'0')='0'