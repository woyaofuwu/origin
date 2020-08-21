SELECT TO_CHAR(a.batch_task_id) batch_task_id,TO_CHAR(a.batch_id) batch_id,       
       TO_CHAR(a.operate_id) operate_id,TO_CHAR(a.trade_id) trade_id,       
       a.batch_oper_type,a.priority,a.cancel_tag,a.serial_number,a.route_eparchy_code,a.db_source,
       b.in_mode_code,b.remove_tag,b.active_flag,c.sms_flag,a.deal_state,
       TO_CHAR(a.refer_time,'yyyy-mm-dd hh24:mi:ss') refer_time,
       TO_CHAR(a.exec_time,'yyyy-mm-dd hh24:mi:ss') exec_time,
       TO_CHAR(a.deal_time,'yyyy-mm-dd hh24:mi:ss') deal_time,     
       DECODE(a.cancel_tag,'0',b.staff_id,a.cancel_staff_id) trade_staff_id,
       DECODE(a.cancel_tag,'0',b.depart_id,a.cancel_depart_id) trade_depart_id,
       DECODE(a.cancel_tag,'0',b.city_code,a.cancel_city_code) trade_city_code,
       DECODE(a.cancel_tag,'0',b.eparchy_code,a.cancel_eparchy_code) trade_eparchy_code,
       c.coding_str1,c.coding_str2,c.coding_str3,c.coding_str4,c.coding_str5,
       a.data1,a.data2,a.data3,a.data4,a.data5,a.data6,a.data7,a.data8,a.data9,a.data10,
       a.data11,a.data12,a.data13,a.data14,a.data15,a.data16,a.data17,a.data18,a.data19,a.data20
  FROM tf_b_trade_batdeal a,tf_b_trade_bat b,tf_b_trade_bat_task c
 WHERE a.operate_id = TO_NUMBER(:DEAL_ID)
   AND a.cancel_tag = :DEAL_TAG
   AND a.deal_state||NULL = :DEAL_STATE
   AND a.batch_id+0 = b.batch_id
   --AND b.remove_tag = '0'
   --AND b.active_flag  = '1'
   AND b.batch_task_id+0 = c.batch_task_id