INSERT INTO tf_f_user_plat_order(partition_id,user_id,serial_number,biz_code,sp_code,product_no,biz_type_code,
org_domain,opr_source,biz_state_code,start_date,end_date,first_date,first_date_mon,gift_serial_number,gift_user_id,
bill_type,price,subscribe_id,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_num4,rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,
rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,rsrv_date1,rsrv_date2,rsrv_date3,rsrv_date4,
rsrv_date5,remark,update_staff_id,update_depart_id,update_time) 
SELECT MOD(user_id,10000),user_id,serial_number,biz_code,sp_code,product_no,biz_type_code,
org_domain,opr_source,biz_state_code,start_date,end_date,first_date,first_date_mon,gift_serial_number,gift_user_id,
bill_type,price,subscribe_id,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_num4,rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,
rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,rsrv_date1,rsrv_date2,rsrv_date3,rsrv_date4,
rsrv_date5,remark,update_staff_id,update_depart_id,SYSDATE 
  FROM tf_b_trade_plat_order
 WHERE partition_id = MOD(:TRADE_ID,10000)
   AND trade_id = :TRADE_ID
   and (rsrv_num1 = to_number(:RSRV_NUM1) or :RSRV_NUM1 is null)
   AND OPER_CODE <> '07'