INSERT INTO tf_b_trade_res(trade_id,accept_month,user_id,user_id_a,res_type_code,res_code,imsi,ki,inst_id,campn_id,start_date,end_date,modify_tag,update_time,update_staff_id,update_depart_id,remark)
SELECT TO_NUMBER(:TRADE_ID),TO_NUMBER(SUBSTRB(:TRADE_ID,5,2)),TO_NUMBER(:USER_ID),-1,a.res_type_code,a.res_code,a.imsi,to_char(nvl(a.ki,'1')) ki, substr(user_id,0,2)||substr(f_sys_getseqid('ZZZZ','seq_inst_id'),3) inst_id,a.campn_id,SYSDATE,TO_DATE('2050-12-31 23:59:59','yyyy-mm-dd hh24:mi:ss'),'0',SYSDATE,'','',''
  FROM tf_f_user_res a 
 WHERE a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)  
   AND a.user_id = TO_NUMBER(:USER_ID)
  -- AND a.res_type_code!='S'
   AND a.end_date = (
       SELECT MAX(r.end_date) FROM tf_f_user_res r
       WHERE r.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
       AND r.user_id = TO_NUMBER(:USER_ID)  
     --  AND r.res_type_code!='S'
       AND end_date > start_date
   )
   AND NOT EXISTS (
       SELECT 1 FROM tf_b_trade_res t
       WHERE t.trade_id = TO_NUMBER(:TRADE_ID)
       AND t.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
       AND t.res_type_code = a.res_type_code
   )