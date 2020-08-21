SELECT eparchy_code,staff_id,recv_day,to_char(recv_fee_num) recv_fee_num,to_char(cancel_num) cancel_num,to_char(recv_fee_cash) recv_fee_cash,to_char(recv_fee_card) recv_fee_card,to_char(recv_fee_check) recv_fee_check,to_char(recv_fee_xfk) recv_fee_xfk,to_char(recv_fee_other) recv_fee_other,to_char(cancel_fee) cancel_fee,to_char(invoice_num) invoice_num,to_char(imprest_num) imprest_num,to_char(rsrv_1) rsrv_1,to_char(rsrv_2) rsrv_2 
  FROM tf_a_dayrecvlog
 WHERE eparchy_code=:EPARCHY_CODE
   AND staff_id=:STAFF_ID 
   AND recv_day=:RECV_DAY