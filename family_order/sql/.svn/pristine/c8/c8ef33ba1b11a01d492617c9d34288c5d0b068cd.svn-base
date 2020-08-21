SELECT f_sys_getcodename('depart_id',depart_id,NULL,NULL) depart_name,
       a.staff_id,
       b.purchase_desc,
       f_sys_getcodename('trade_type_code',a.trade_type_code,NULL,NULL) trade_type,
       SUM(a.sale_num) trade_num,
       SUM(a.reward_fee/100) agent_fee
  FROM ts_s_purchase_reward a,td_b_purchasetrade b
 WHERE a.clct_day >= :start_date
   AND a.clct_day <= :end_date
   AND a.depart_id = :depart_id
   AND (a.staff_id = :staff_id OR :staff_id ='%')
   AND a.purchase_mode = b.purchase_mode
   AND a.purchase_attr = b.purchase_attr
   AND b.purchase_mode = 'H1'
 GROUP BY f_sys_getcodename('depart_id',depart_id,NULL,NULL),a.staff_id,b.purchase_desc,
           f_sys_getcodename('trade_type_code',a.trade_type_code,NULL,NULL)