INSERT INTO tf_b_trade_res(trade_id,accept_month,user_id,user_id_a,res_type_code,res_code,imsi,ki,inst_id,campn_id,start_date,end_date,modify_tag,update_time,update_staff_id,update_depart_id,remark,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_num4,rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_date1,rsrv_date2,rsrv_date3,rsrv_tag1,rsrv_tag2,rsrv_tag3)
SELECT TO_NUMBER(:TRADE_ID),TO_NUMBER(SUBSTRB(:TRADE_ID,5,2)),a.user_id,a.user_id_a,a.res_type_code,a.res_code,a.imsi,to_char(nvl(a.ki,'1')) ki, substr(trade_id,0,2)||substr(f_sys_getseqid('ZZZZ','seq_inst_id'),3) inst_id,a.campn_id,SYSDATE,a.end_date,'0',a.update_time,a.update_staff_id,a.update_depart_id,a.remark,a.rsrv_num1,a.rsrv_num2,a.rsrv_num3,a.rsrv_num4,a.rsrv_num5,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,a.rsrv_date1,a.rsrv_date2,a.rsrv_date3,a.rsrv_tag1,a.rsrv_tag2,a.rsrv_tag3
 FROM tf_b_trade_res_bak a 
 WHERE a.trade_id = TO_NUMBER(:OLDTRADE_ID)
 And a.accept_month = TO_NUMBER(SUBSTR(:OLDTRADE_ID,5,2)) 
  AND SYSDATE < a.end_date
  -- AND a.res_type_code!='S'
  AND NOT EXISTS (
      SELECT 1 FROM tf_b_trade_res r
      WHERE r.trade_id = TO_NUMBER(:TRADE_ID)
      AND r.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
      AND r.res_type_code = a.res_type_code
  )