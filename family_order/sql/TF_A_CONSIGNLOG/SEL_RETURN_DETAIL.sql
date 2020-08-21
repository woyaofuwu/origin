select a.serial_number,to_char(a.acct_id) acct_id ,b.pay_name pay_name,a.bcyc_id bcyc_id ,
  to_char(a.adeb_fee) adeb_fee,to_char(a.adeblate_fee) adeblate_fee,to_char(a.aspay_fee) aspay_fee,
  to_char(a.charge_id) charge_id,to_char(b.return_time,'yyyy-mm-dd hh24:mi:ss') return_time,b.return_staff_id return_staff_id,b.bank bank
  from tf_a_consigninfolog a,
  (select pay_name,return_time,return_staff_id,consign_id,bank
  from tf_a_consignlog where return_tag='1'
  and recv_acyc_id = :RECV_ACYC_ID
  and eparchy_code = :EPARCHY_CODE) b
  where a.recv_acyc_id = :RECV_ACYC_ID
  and a.mconsign_id = b.consign_id 
  and a.eparchy_code = :EPARCHY_CODE
  and (:BEGIN_TIME IS NULL or b.return_time >= to_date(:BEGIN_TIME,'yyyymmddhh24mi'))
  and (:BEGIN_TIME IS NULL or b.return_time <= to_date(:END_TIME,'yyyymmddhh24mi'))