SELECT t1.trade_id, t1.user_id, t1.rsrv_str1, t1.rsrv_str2, t1.rsrv_str3, t1.rsrv_str4, t1.rsrv_str5, t1.rsrv_tag1,
       t2.serial_number, t2.product_name, t2.package_name
  FROM tf_f_user_other t1, tf_f_user_sale_active t2
 WHERE t1.rsrv_value_code = :RSRV_VALUE_CODE
   AND t1.trade_id = t2.relation_trade_id
   AND t1.start_date >= TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')
   AND t1.start_date < TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')+1
   AND t1.rsrv_tag1 = :RSRV_TAG1
   AND t2.serial_number = :SERIAL_NUMBER