Insert INTO tf_b_trade_svc(trade_id,accept_month,user_id,user_id_a,product_id,package_id,service_id,main_tag,inst_id,campn_id,start_date,end_date,modify_tag,update_time,update_staff_id,update_depart_id,remark,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_num4,rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_date1,rsrv_date2,rsrv_date3,rsrv_tag1,rsrv_tag2,rsrv_tag3)
SELECT TO_NUMBER(:TRADE_ID),TO_NUMBER(SUBSTRB(:TRADE_ID,5,2)),a.user_id,user_id_a,a.product_id,a.package_id,service_id,main_tag,
substr(trade_id,0,2)||substr(f_sys_getseqid('ZZZZ','seq_inst_id'),3) inst_id,campn_id,SYSDATE,end_date,'0',a.update_time,a.update_staff_id,a.update_depart_id,a.remark,a.rsrv_num1,a.rsrv_num2,a.rsrv_num3,a.rsrv_num4,a.rsrv_num5,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,a.rsrv_date1,a.rsrv_date2,a.rsrv_date3,a.rsrv_tag1,a.rsrv_tag2,a.rsrv_tag3
 FROM tf_b_trade_svc_bak a 
 WHERE trade_id = TO_NUMBER(:OLDTRADE_ID)
  AND accept_month = TO_NUMBER(SUBSTR(:OLDTRADE_ID,5,2))
  AND end_date > Sysdate   
  AND Not EXISTS (SELECT 1 FROM td_s_commpara a
                where a.subsys_code ='CSM'
                and a.param_attr = '6017'
                And  a.param_code='2'
                and a.para_code1 = a.service_id   
                And ( substr(a.eparchy_code,3)=substr(a.trade_id,0,2)  Or  eparchy_code='ZZZZ')
                and a.end_date > sysdate
                )