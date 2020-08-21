INSERT INTO tf_f_user_mbmp_sub(partition_id,user_id,sp_id,biz_type_code,org_domain,opr_source,sp_svc_id,start_date,end_date,biz_state_code,rsrv_str5,update_staff_id,update_depart_id,update_time,serial_number)

select mod(a.user_id,10000),a.user_id,a.sp_id,a.biz_type_code,a.org_domain,a.opr_source,a.sp_svc_id,sysdate,to_date('20501231','yyyymmdd'),decode(a.biz_state_code,'B','A','E','P',a.biz_state_code),'07',a.trade_staff_id,a.trade_depart_id,a.trade_time,b.serial_number 

  from tf_b_trade_mbmp_sub a,tf_b_trade b 

 where a.trade_id=to_number(:TRADE_ID)

   AND b.trade_id = a.trade_id