SELECT a.eparchy_code,
       a.item_name feeitem_name,
       NVL(SUM(a.fee),0) real_fee,
       a.fee_day clct_day,
       a.out_day create_date
  FROM ts_r_mis_02 a
 WHERE a.fee_day >= :start_date
   AND a.fee_day <= :end_date
   AND a.depart_id >= :start_depart_id
   AND a.depart_id <= :end_depart_id
 GROUP BY a.eparchy_code,a.item_name,a.fee_day,a.out_day