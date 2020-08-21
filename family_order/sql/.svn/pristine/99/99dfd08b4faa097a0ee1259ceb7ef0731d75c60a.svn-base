SELECT a.serial_number,
       c.deposit/100 advance_pay,
       c.purchase_desc,
       c.mgift_deposit,
       f_sys_getcodename('product_id',a.product_id,NULL,NULL) product_name,
       1 trade_num,
       TO_CHAR(a.accept_date,'YYYYMMDD HH24:MI:SS') accept_date,
       a.trade_staff_id,
       a.trade_depart_id
  FROM tf_bh_trade a ,tf_b_trade_purchase b ,td_b_purchasetrade c
 WHERE a.trade_id = b.trade_id
   AND a.accept_date >= TO_DATE(:start_date,'YYYYMMDD')
   AND a.accept_date <  TO_DATE(:end_date,'YYYYMMDD') + 1
   AND a.trade_depart_id LIKE :depart_id
   AND a.trade_staff_id LIKE :staff_id
   AND b.purchase_mode = c.purchase_mode
   AND b.purchase_attr = c.purchase_attr
   AND c.purchase_mode IN ('Y1','Y2','Y3','Y4','Y5','Y6')
 ORDER BY accept_date