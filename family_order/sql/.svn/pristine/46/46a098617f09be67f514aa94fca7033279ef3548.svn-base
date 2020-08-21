SELECT to_char(a.user_id) user_id,a.partition_id,a.serial_number,a.eparchy_code,
       a.channel_code,a.execute_id,b.execute_desc,b.id_type,b.id,b.priority,b.description,
       to_char(NVL(a.start_date,b.start_date),'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(b.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,
       to_char(a.in_time,'yyyy-mm-dd hh24:mi:ss') in_time,
       a.mod_name,to_char(a.trade_id) trade_id,
       to_char(a.deal_time,'yyyy-mm-dd hh24:mi:ss') deal_time,
       a.deal_tag,a.deal_staff_id,a.deal_depart_id,a.deal_eparchy_code,
       b.rec_type rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5 
  FROM tf_f_recommend_new a,td_b_recommend b
 WHERE a.user_id = TO_NUMBER(:USER_ID)
   AND a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND a.channel_code||NULL = :CHANNEL_CODE
   AND a.execute_id = :EXECUTE_ID
   AND a.channel_code||NULL = b.channel_code
   AND a.execute_id = b.execute_id