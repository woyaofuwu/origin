SELECT a.serial_number,a.apply_no,to_char(a.apply_batch_id) apply_batch_id,a.imsi,a.sim_card_no,a.eparchy_code,a.city_code,a.stock_id,a.staff_id,a.staff_name,to_char(a.use_limit) use_limit,a.trade_func,a.purpose_declare,a.card_kind_code,a.card_state_code,to_char(a.use_time,'yyyy-mm-dd hh24:mi:ss') use_time,to_char(a.return_time_limit,'yyyy-mm-dd hh24:mi:ss') return_time_limit,to_char(a.return_time_fact,'yyyy-mm-dd hh24:mi:ss') return_time_fact,to_char(a.open_time,'yyyy-mm-dd hh24:mi:ss') open_time,a.remind_code,to_char(a.remind_date,'yyyy-mm-dd hh24:mi:ss') remind_date,a.return_staff_id,a.remark,a.rsrv_tag1,a.rsrv_tag2,a.rsrv_tag3,to_char(a.rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(a.rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(a.rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,a.rsrv_str6,a.rsrv_str7,a.rsrv_num1,a.rsrv_num2,a.rsrv_num3,0 x_tag 
  FROM tf_b_testcard_use a,TF_B_RESAPPLY_MAIN b
 WHERE (:EPARCHY_CODE is null or a.eparchy_code=:EPARCHY_CODE)
   AND (:CITY_CODE is null or a.city_code=:CITY_CODE)
   AND (:STOCK_ID is null or a.stock_id=:STOCK_ID)
   AND (:CARD_KIND_CODE is null or a.card_kind_code=:CARD_KIND_CODE)
   AND (:CARD_STATE_CODE is null or a.card_state_code=:CARD_STATE_CODE)
   AND b.apply_date>=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')
   AND b.apply_date<=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')
   AND a.apply_no=b.apply_no
   AND a.apply_batch_id=b.apply_batch_id