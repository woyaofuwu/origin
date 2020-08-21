SELECT /*+index (a PK_TF_F_USER_PLAT_ORDER)*/ partition_id,to_char(user_id) user_id,serial_number,biz_code,sp_code,product_no,biz_type_code,org_domain,opr_source,biz_state_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(first_date,'yyyy-mm-dd hh24:mi:ss') first_date,to_char(first_date_mon,'yyyy-mm-dd hh24:mi:ss') first_date_mon,gift_serial_number,gift_user_id,bill_type,price,to_char(subscribe_id) subscribe_id,rsrv_num1,rsrv_num2,rsrv_num3,to_char(rsrv_num4) rsrv_num4,to_char(rsrv_num5) rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,to_char(rsrv_date4,'yyyy-mm-dd hh24:mi:ss') rsrv_date4,to_char(rsrv_date5,'yyyy-mm-dd hh24:mi:ss') rsrv_date5,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM tf_f_user_plat_order a
 where user_id = to_number(:USER_ID)
   and partition_id = mod(to_number(:USER_ID),10000)
   and end_date > sysdate
   and exists (select 1 from tf_b_trade_plat_order
                where partition_id = MOD(to_number(:TRADE_ID),10000)
                  and trade_id = to_number(:TRADE_ID)
                  and sp_code = a.sp_code
                  and biz_code = a.biz_code
                  and biz_state_code = a.biz_state_code
                  and biz_state_code <> 'E')