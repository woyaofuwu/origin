SELECT clct_day,
        staff_id,
       f_sys_getcodename('staff_id',staff_id,NULL,NULL) staff_name,
       f_sys_getcodename('depart_id',depart_id,NULL,NULL) depart_name,
       '开户预存款' feeitem_name,
       SUM(NVL(a.fee,0))/100 real_fee,
       SUM(NVL(a.sale_num,0)) trade_num
  FROM ts_s_pfee_day_staff a, td_sd_payfeemode b
 WHERE clct_day >= :start_date
   AND clct_day <= :end_date
   AND depart_id >= :start_depart_id
   AND depart_id <= :end_depart_id
   AND TO_CHAR(a.pay_fee_mode_code) = b.pay_fee_mode_code  -- 注意，不把右边转化成NUMBER型主要考虑到营业费用会有CHAR型出现。
   AND b.fee_type_code = '1' -- 帐务费用
   AND b.act_type = '1'      -- 营业员收费
   AND b.act_flag = '1'       -- 作用标志，有效
   AND a.charge_source_code ='2'
 GROUP BY a.clct_day,a.staff_id,depart_id