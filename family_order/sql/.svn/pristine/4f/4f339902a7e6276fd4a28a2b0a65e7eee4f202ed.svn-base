SELECT '022' province_code,
       '现金' fee_type,
       NVL(SUM(a.fee),0) fee,
       clct_day,
       TO_CHAR(TO_DATE(clct_day,'YYYYMMDD')+1,'YYYYMMDD') create_date
  FROM ts_s_pfee_day_staff a, td_sd_payfeemode b 
 WHERE a.clct_day >= :start_date
   AND a.clct_day <= :end_date
   AND a.depart_id >= :start_depart_id
   AND a.depart_id <= :end_depart_id
   AND a.charge_source_code IN (SELECT c.charge_source_code 
                                  FROM td_sd_chargesource c 
                                 WHERE c.act_type = '1' 
                                   AND c.valid_tag = '1'
                               )
   AND TO_CHAR(a.pay_fee_mode_code) = b.pay_fee_mode_code
   AND b.act_type = '1'
   AND b.act_flag = '1'
   AND b.code1 = '0' 
 GROUP BY clct_day