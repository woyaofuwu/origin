SELECT f_sys_getcodename('depart_id',a.trade_depart_id,NULL,NULL) depart_name,
       a.trade_staff_id staff_id,
       b.purchase_desc,
       f_sys_getcodename('trade_type_code',trade_type_code,NULL,NULL) trade_type,
       1 trade_num,
       (c.discnt_gift_months * c.mgift_deposit)/100 advance_pay,
       DECODE(trade_type_code,240,a.rsrv_str9,-a.rsrv_str9)/100 balance,
       a.remark remark
  FROM tf_bh_trade a ,tf_b_trade_purchase b,td_b_purchasetrade c
 WHERE a.trade_type_code IN (240,241)
   AND a.trade_depart_id = :depart_id
   AND (a.trade_staff_id = :staff_id OR :staff_id = '%')
   AND a.accept_date >= TO_DATE(:start_date,'YYYYMMDD')
   AND a.accept_date <= TO_DATE(:end_date,'YYYYMMDD') + 1
   AND a.trade_id = b.trade_id
   AND b.purchase_attr = c.purchase_attr
   AND c.purchase_mode = 'H1'