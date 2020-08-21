SELECT a.staff_id,
       f_sys_getcodename('staff_id',a.staff_id,NULL,NULL) staff_name,
       f_sys_getcodename('depart_id',a.depart_id,NULL,NULL) depart_name,
       a.clct_day,	 
	f_sys_getcodename('trade_type_code',a.trade_type_code,NULL,NULL) trade_type,
       NVL(SUM(DECODE(a.cancel_tag,'0',a.trade_count,0)),0) trade_num,
       NVL(SUM(DECODE(a.cancel_tag,'0',0,a.trade_count)),0) cancel_num
  FROM ts_s_trade_day_staff a 
 WHERE a.clct_day >= :start_date
   AND a.clct_day <= :end_date
   AND a.staff_id >= :start_staff_id
   AND a.staff_id <= :end_staff_id
   --and (a.trade_type_code = :trade_type_code or :trade_type_code = '%')
 GROUP BY a.staff_id,a.depart_id,a.clct_day,a.trade_type_code