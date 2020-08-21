SELECT depart_id,
       staff_id,
       clct_day,
       SUM(sale_num) recv_fee_num,
       SUM(sale_money)/1000 recv_fee,
       SUM(DECODE(cancel_tag,'0',0,sale_num)) cancel_num,
       SUM(DECODE(cancel_tag,'0',0,sale_money))/1000 cancel_fee 
  FROM ts_s_ibpfee_day_staff
 WHERE bip_status = '0'  
   AND clct_day >= :start_date
   AND clct_day <= :end_date
   AND (depart_id = :depart_id OR :depart_id = '%')
 GROUP BY depart_id,staff_id,clct_day