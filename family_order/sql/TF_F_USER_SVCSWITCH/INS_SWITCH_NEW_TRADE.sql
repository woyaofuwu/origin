INSERT INTO tf_f_user_svcswitch(partition_id,user_id,service_type,deal_flag,start_date,end_date,update_time)
SELECT MOD(to_number(:USER_ID),10000),to_number(:USER_ID),para_code1,decode(open_tag,'0000','0','1'),
       to_date(:START_DATE, 'yyyy-mm-dd hh24:mi:ss'),to_date('2050-12-31 23:59:59','yyyy-mm-dd hh24:mi:ss'),
       SYSDATE
  FROM tf_b_trade_plat_register a,td_s_commpara b
 WHERE a.partition_id = MOD(to_number(:TRADE_ID),10000)
   AND a.trade_id = to_number(:TRADE_ID)
   AND a.user_id = to_number(:USER_ID)
   AND a.biz_type_code = b.param_code
   AND a.oper_code in ('01','80')
   AND b.param_attr = 934
   AND b.subsys_code = 'CSM'
   AND b.eparchy_code = 'ZZZZ'
   AND SYSDATE BETWEEN a.Start_Date+0 AND a.end_date+0