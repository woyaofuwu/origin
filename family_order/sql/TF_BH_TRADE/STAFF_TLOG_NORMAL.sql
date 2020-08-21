SELECT TO_CHAR(a.trade_id) trade_id,
       a.trade_staff_id staff_id,
       f_sys_getcodename('staff_id',a.trade_staff_id,NULL,NULL) staff_name,
       f_sys_getcodename('depart_id',a.trade_depart_id,NULL,NULL) depart_name,
       f_sys_getcodename('trade_type_code',a.trade_type_code,NULL,NULL) trade_type,
       TO_CHAR(a.accept_date,'YYYY-MM-DD HH24:MI:SS') accept_date,
       a.serial_number,
       f_sys_getcodename('brand_code',a.brand_code,NULL,NULL) brand,
       a.cust_name,
       1 trade_num,
       '正常' cancel_tag,
       NULL cancel_staff_id
  FROM tf_bh_trade a
 WHERE a.cancel_tag IN ('0','1')
   AND a.trade_staff_id >= :start_staff_id
   AND a.trade_staff_id <= :end_staff_id
   AND a.accept_date >= TO_DATE(:start_date,'YYYYMMDD')
   AND a.accept_date <  TO_DATE(:end_date,'YYYYMMDD') + 1
   AND (A.TRADE_TYPE_CODE = :TRADE_TYPE_CODE OR :TRADE_TYPE_CODE = '0')
 UNION ALL
SELECT TO_CHAR(a.trade_id) trade_id,
       a.trade_staff_id staff_id,
       f_sys_getcodename('staff_id',a.trade_staff_id,NULL,NULL) staff_name,
       f_sys_getcodename('depart_id',a.trade_depart_id,NULL,NULL) depart_name,
       f_sys_getcodename('trade_type_code',a.trade_type_code,NULL,NULL) trade_type,
       TO_CHAR(a.accept_date,'YYYY-MM-DD HH24:MI:SS') accept_date,
       a.serial_number,
       f_sys_getcodename('brand_code',a.brand_code,NULL,NULL) brand,
       a.cust_name,
       1 trade_num,
       '正常' cancel_tag,
       NULL cancel_staff_id
  FROM tf_b_trade a
 WHERE a.cancel_tag IN ('0','1')
   AND a.trade_staff_id >= :start_staff_id
   AND a.trade_staff_id <= :end_staff_id
   AND a.accept_date >= TO_DATE(:start_date,'YYYYMMDD')
   AND a.accept_date <  TO_DATE(:end_date,'YYYYMMDD') + 1
   AND (A.TRADE_TYPE_CODE = :TRADE_TYPE_CODE OR :TRADE_TYPE_CODE = '0')