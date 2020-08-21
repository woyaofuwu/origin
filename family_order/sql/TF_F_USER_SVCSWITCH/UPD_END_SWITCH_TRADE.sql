UPDATE tf_f_user_svcswitch
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),
       update_time=SYSDATE  
 WHERE Partition_id = MOD(to_number(:USER_ID),10000)
   AND user_id = to_number(:USER_ID)
   AND SYSDATE BETWEEN start_date AND end_date
   AND service_type IN (SELECT para_code1 FROM td_s_commpara a,tf_b_trade_plat_register b
                         WHERE a.param_attr = 934
                           AND a.subsys_code = 'CSM'
                           AND a.eparchy_code = 'ZZZZ'
                           AND a.para_code2 = '0'
                           AND SYSDATE BETWEEN a.start_date AND a.end_date
                           AND b.partition_id = MOD(to_number(:TRADE_ID),10000)
                           AND b.trade_id = to_number(:TRADE_ID)
                           AND b.user_id = :USER_ID
                           AND a.param_code = b.biz_type_code
                           AND b.oper_code IN ('02','80'))