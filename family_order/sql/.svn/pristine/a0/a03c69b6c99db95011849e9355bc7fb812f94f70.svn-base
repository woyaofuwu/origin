SELECT to_char(consign_id) consign_id,acyc_id,bcyc_id,to_char(aspay_fee) aspay_fee,to_char(aspay_late_fee) aspay_late_fee,city_code,city_name,pay_name,serial_number,remark,nvl(return_tag,'0') return_tag,to_char(return_time,'yyyy-mm-dd hh24:mi:ss') return_time,refuse_reason_code,to_char(acct_id) acct_id,recv_acyc_id
  FROM tf_a_consignlog
 WHERE consign_id=TO_NUMBER(:CONSIGN_ID)
   AND nvl(return_tag,'0')='3'