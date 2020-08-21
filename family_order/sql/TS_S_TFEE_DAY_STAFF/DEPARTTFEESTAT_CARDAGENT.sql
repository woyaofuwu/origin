SELECT a.staff_id,
       f_sys_getcodename('staff_id',staff_id,NULL,NULL) staff_name,
       f_sys_getcodename('depart_id',depart_id,NULL,NULL) depart_name,
       a.clct_day,
       '后台有价卡销售※'||NVL(f_sys_getcodename('feeitem_code',a.fee_type_code,NULL,NULL),a.fee_type_code) fee_type,
       NVL(SUM(NVL(a.fee/100,0)),0) real_fee,
       NVL(SUM(DECODE(a.cancel_tag,'0',NVL(a.sale_num,0),0)),0) trade_num,
       NVL(SUM(DECODE(a.cancel_tag,'1',NVL(a.sale_num,0),0)),0) cancel_num
  FROM ts_s_tfee_day_staff a
 WHERE a.depart_id >= :start_depart_id
   AND a.depart_id <= :end_depart_id
   AND a.clct_day >= :start_date
   AND a.clct_day <= :end_date
   AND a.fee_mode = '0'
   AND ROWNUM < 1
  GROUP BY a.staff_id,a.depart_id,a.clct_day,a.fee_type_code