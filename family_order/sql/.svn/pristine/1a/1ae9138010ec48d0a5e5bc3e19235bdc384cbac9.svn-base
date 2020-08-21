SELECT clct_day,
        staff_id,
       f_sys_getcodename('staff_id',staff_id,NULL,NULL) staff_name,
       f_sys_getcodename('depart_id',depart_id,NULL,NULL) depart_name,
        '话费金额' feeitem_name,
        SUM(NVL(a.fee,0))/100 real_fee,
        SUM(NVL(a.sale_num,0)) trade_num
   FROM ts_s_pfee_day_staff a, td_sd_payfeemode b
 WHERE clct_day >= :start_date
   AND clct_day <= :end_date
   AND staff_id >= :start_staff_id
   AND staff_id <= :end_staff_id
   AND TO_CHAR(a.pay_fee_mode_code) = b.pay_fee_mode_code  
   AND b.fee_type_code = '1' 
   AND b.act_type = '1'      
   AND b.act_flag = '1'       
   AND a.cancel_tag = '0'
   AND a.charge_source_code IN (SELECT c.charge_source_code
                                  FROM td_sd_chargesource c
                                 WHERE c.act_type = '1'    
                                   AND c.valid_tag = '1'
                                   AND c.charge_source_code NOT IN ('2','14')
                               )
 GROUP BY a.clct_day,a.staff_id,depart_id