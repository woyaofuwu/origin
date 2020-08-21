SELECT clct_day,
        staff_id,
       f_sys_getcodename('staff_id',a.staff_id,NULL,NULL) staff_name,
       f_sys_getcodename('depart_id',a.depart_id,NULL,NULL) depart_name,
       '返销金额' FEEITEM_NAME,
       SUM(NVL(a.fee,0))/100 real_fee,
       SUM(NVL(a.sale_num,0)) trade_num
  FROM ts_s_pfee_day_staff a, td_sd_payfeemode b,td_sd_chargesource c
 WHERE a.staff_id >= :start_staff_id
   AND a.staff_id <= :end_staff_id
   AND a.clct_day >= :start_date
   AND a.clct_day <= :end_date
   AND TO_CHAR(a.pay_fee_mode_code) = b.pay_fee_mode_code  
   AND b.fee_type_code = '1'
   AND b.act_type = '1' 
   AND b.act_flag = '1'
   AND a.cancel_tag != '0'
   AND a.charge_source_code = c.charge_source_code
   AND c.act_type = '1' 
   AND c.valid_tag = '1' 
 GROUP BY a.clct_day,a.staff_id,depart_id