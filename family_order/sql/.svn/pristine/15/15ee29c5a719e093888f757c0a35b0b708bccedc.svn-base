SELECT clct_day,
        staff_id,
       f_sys_getcodename('staff_id',staff_id,NULL,NULL) staff_name,
       f_sys_getcodename('depart_id',depart_id,NULL,NULL) depart_name,
       f_sys_getcodename('trade_type_code',trade_type_code,NULL,NULL)||'※'||f_sys_getcodename('foregift_code',fee_type_code,NULL,NULL) feeitem_name,
       SUM(NVL(fee,0))/100 real_fee,
       SUM(NVL(sale_num,0)) trade_num
  FROM ts_s_tfee_day_staff
 WHERE clct_day >= :start_date
   AND clct_day <= :end_date
   AND staff_id >= :start_staff_id
   AND staff_id <= :end_staff_id
   AND fee_mode = '1'
 GROUP BY clct_day,staff_id,depart_id,trade_type_code,fee_type_code