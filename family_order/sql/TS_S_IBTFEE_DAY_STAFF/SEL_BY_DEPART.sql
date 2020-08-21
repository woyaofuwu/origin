SELECT depart_id,
       staff_id,
       clct_day,
       SUM(DECODE(cancel_tag,'0',sale_num,-sale_num)) recv_fee_num,
       SUM(DECODE(cancel_tag,'0',sale_money+serv_money,-sale_money-serv_money))/100 recv_fee,
       SUM(DECODE(cancel_tag,'0',0,sale_num)) cance_num,
       SUM(DECODE(cancel_tag,'0',0,sale_money+serv_money))/100 cancel_fee
  FROM ts_s_ibtfee_day_staff
 WHERE bip_status = '0'
   AND clct_day >= :start_date
   AND clct_day <= :end_date
   AND (depart_id = :depart_id OR :depart_id = '%')
 GROUP BY depart_id,staff_id,clct_day