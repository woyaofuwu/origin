--IS_CACHE=N
SELECT serial_number,
       intf_trade_id,
       TO_CHAR(pay_fee/100,'FM9990.00') pay_fee,
       to_char(accept_date,'yyyy-MM-dd hh24:mi:ss') accept_date,
       to_char(oper_time,'yyyy-MM-dd hh24:mi:ss') oper_time,
       recon_state,
       to_char(update_time,'yyyy-MM-dd hh24:mi:ss') update_time,
       cancel_flag
  FROM ti_b_small_payment_log_result
 WHERE 1=1
   AND accept_date >= to_date(:START_TIME, 'yyyy-mm-dd')
   AND accept_date <= to_date(:END_TIME, 'yyyy-mm-dd')
   AND (:RECON_STATE IS NULL OR recon_state = :RECON_STATE)
   AND (:CANCEL_FLAG IS NULL OR CANCEL_FLAG = :CANCEL_FLAG)
 order by accept_date, recon_state