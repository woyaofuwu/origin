SELECT TO_CHAR(a.trade_id) trade_id,
       a.trade_staff_id staff_id,
       f_sys_getcodename('staff_id',a.trade_staff_id,NULL,NULL) staff_name,
       f_sys_getcodename('depart_id',a.trade_depart_id,NULL,NULL) depart_name,
       DECODE(b.tag,'1','卡号分离开户','2','正常开户','3','空中开户')  trade_type,
       TO_CHAR(a.accept_date,'YYYY-MM-DD HH24:MI:SS') accept_date,
       f_sys_getcodename('area_code',a.eparchy_code,NULL,NULL) eparchy_name,
       a.serial_number,
       f_sys_getcodename('brand_code',a.brand_code,NULL,NULL) brand,
       a.cust_name,
       1 trade_num,
       0 cancel_num,
       (A.OPER_FEE+A.FOREGIFT+A.ADVANCE_PAY)/100 Trade_fee,
       '返销' cancel_tag,
       NULL cancel_staff_id,
              a.advance_pay/100 advance_pay,
       b.qc_name,
       b.qz_name
  FROM tf_bh_trade a,ts_r_mx_cdr_yy b
 WHERE a.trade_id=b.trade_id
   AND a.cancel_tag IN ('2')
   AND a.trade_depart_id >= :start_depart_id
   AND a.trade_depart_id <= :end_depart_id
   AND a.accept_date >= TO_DATE(:start_date,'YYYYMMDD')
   AND a.accept_date <  TO_DATE(:end_date,'YYYYMMDD') + 1
   AND (b.tag = :TRADE_TYPE_CODE OR :TRADE_TYPE_CODE = '0')
 UNION ALL
SELECT TO_CHAR(a.trade_id) trade_id,
       a.trade_staff_id staff_id,
       f_sys_getcodename('staff_id',a.trade_staff_id,NULL,NULL) staff_name,
       f_sys_getcodename('depart_id',a.trade_depart_id,NULL,NULL) depart_name,
       DECODE(b.tag,'1','卡号分离开户','2','正常开户','3','空中开户')  trade_type,
       TO_CHAR(a.accept_date,'YYYY-MM-DD HH24:MI:SS') accept_date,
       f_sys_getcodename('area_code',a.eparchy_code,NULL,NULL) eparchy_name,
       a.serial_number,
       f_sys_getcodename('brand_code',a.brand_code,NULL,NULL) brand,
       a.cust_name,
       1 trade_num,
       0 cancel_num,
       (A.OPER_FEE+A.FOREGIFT+A.ADVANCE_PAY)/100 Trade_fee,
       '返销' cancel_tag,
       NULL cancel_staff_id,
             a.advance_pay/100 advance_pay,
       b.qc_name,
       b.qz_name
  FROM tf_b_trade a,ts_r_mx_cdr_yy b
 WHERE a.trade_id=b.trade_id
   AND a.cancel_tag IN ('2')
   AND a.trade_depart_id >= :start_depart_id
   AND a.trade_depart_id <= :end_depart_id
   AND a.accept_date >= TO_DATE(:start_date,'YYYYMMDD')
   AND a.accept_date <  TO_DATE(:end_date,'YYYYMMDD') + 1
   AND (b.tag = :TRADE_TYPE_CODE OR :TRADE_TYPE_CODE = '0')