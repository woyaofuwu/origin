SELECT t1.trade_id, t1.user_id, t1.rsrv_str1, t1.rsrv_str2, t1.rsrv_str3, t1.rsrv_str4, t1.rsrv_str5, t1.rsrv_tag1,
       '240' trade_type_code,t2.accept_date,t2.serial_number, t2.product_id,t2.product_name, t2.package_id,t2.package_name,t2.oper_fee/100 oper_fee, t2.advance_pay/100 advance_pay, t2.foregift/100 foregift,
       t3.res_code
  FROM tf_f_user_other t1, tf_f_user_sale_active t2, tf_f_user_sale_goods t3
 WHERE t1.partition_id = t2.partition_id and t2.partition_id=t3.partition_id
 and t1.user_id=t2.user_id
 and t2.user_id=t3.user_id
 and t1.trade_id = t2.relation_trade_id
 and t2.relation_trade_id=t3.relation_trade_id
 and t2.product_id=t3.product_id
 and t2.package_id=t3.package_id
 and t2.partition_id = mod(:USER_ID,10000)
 and t2.user_id = to_number(:USER_ID)
 and t1.rsrv_value_code = :RSRV_VALUE_CODE
 and t2.process_tag='0'
 and nvl(t2.rsrv_date2,t2.end_date)>sysdate
 order by t2.accept_date desc