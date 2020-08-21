SELECT a.staff_id,
       f_sys_getcodename('staff_id',a.staff_id,NULL,NULL) staff_name,
       a.trade_depart_id depart_id,
       f_sys_getcodename('depart_id',a.trade_depart_id,NULL,NULL) depart_name,
       a.clct_day,	 
       DECODE(a.tag,'1','卡号分离开户','2','正常开户','3','空中开户')  trade_type,
       NVL(SUM(DECODE(a.cancel_tag,'0',a.trade_count,0)),0) trade_num,
       NVL(SUM(DECODE(a.cancel_tag,'0',0,a.trade_count)),0) cancel_num,
              NVL(SUM(a.advance_pay),0) advance_pay,
       a.QC_NAME,
       a.QZ_NAME
  FROM TS_S_TRADE_DAY_YY a 
 WHERE a.clct_day >= :start_date
   AND a.clct_day <= :end_date
   AND a.trade_depart_id >= :start_depart_id
   AND a.trade_depart_id <= :end_depart_id
   and a.tag = :trade_type_code 
 GROUP BY a.trade_depart_id,a.staff_id,a.clct_day,a.TAG,a.QC_NAME,a.QZ_NAME