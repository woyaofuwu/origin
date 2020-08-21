SELECT a.staff_id,
       f_sys_getcodename('staff_id',a.staff_id,NULL,NULL) staff_name,
       f_sys_getcodename('depart_id',a.depart_id,NULL,NULL) depart_name,
       a.clct_day,
       b.pay_fee_mode,
       NVL(SUM(DECODE(a.cancel_tag,'0',DECODE(a.charge_source_code,2,a.fee/100),0)),0) advance_pay,
       NVL(SUM(DECODE(a.cancel_tag,'0',DECODE(a.charge_source_code,2,0,14,0,a.fee/100),0)),0) recv_fee,
       NVL(SUM(DECODE(a.cancel_tag,'0',0,a.fee/100)),0) cancel_fee,
       NVL(SUM(DECODE(a.cancel_tag,'0',DECODE(a.charge_source_code,14,a.fee/100),0)),0) back_fee,
       NVL(SUM(a.fee/100),0)  real_fee,
       NVL(SUM(DECODE(a.cancel_tag,'0',a.sale_num,0)),0) recv_fee_num,
       NVL(SUM(DECODE(a.cancel_tag,'0',0,sale_num)),0) cancel_num
  FROM ts_s_pfee_day_staff a,td_sd_payfeemode b
 WHERE a.staff_id >= :start_staff_id
   AND a.staff_id <= :end_staff_id
   AND a.clct_day >= :start_date
   AND a.clct_day <= :end_date
   AND TO_CHAR(a.pay_fee_mode_code) = b.pay_fee_mode_code   
   AND b.fee_type_code = '1' 
   AND b.act_type = '1'      
   AND b.act_flag = '1'    
   AND a.charge_source_code IN (SELECT c.charge_source_code
                                  FROM td_sd_chargesource c
                                 WHERE c.act_type = '1' 
                                   AND c.valid_tag = '1'
                               )
 GROUP BY a.staff_id,a.depart_id,a.clct_day,b.pay_fee_mode