SELECT a.user_id,a.foregift_code,a.to_char(money) money,a.invoice_no,a.finishtag,to_char(b.accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date,b.fee_staff_id,a.in_trade_id,to_char(b.cancel_date,'yyyy-mm-dd hh24:mi:ss') cancel_date,b.cancel_staff_id,a.trade_id
  FROM TF_F_USER_FOREGIFT_INVOICE a,tf_b_trade b
 WHERE a.invoice_no=TO_NUMBER(:invoice_no) and b.invoice_no=a.invoice_no