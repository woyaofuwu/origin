INSERT INTO tf_f_user_plat_register(partition_id,user_id,serial_number,biz_type_code,org_domain,opr_source,
passwd,biz_state_code,open_tag,svc_level,start_date,end_date,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_num4,rsrv_num5,
rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_date1,rsrv_date2,rsrv_date3,remark,update_staff_id,
update_depart_id,update_time) 
SELECT MOD(User_id,10000),user_id,serial_number,biz_type_code,org_domain,opr_source,
passwd,biz_state_code,open_tag,svc_level,start_date,end_date,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_num4,rsrv_num5,
rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_date1,rsrv_date2,rsrv_date3,remark,update_staff_id,
update_depart_id,SYSDATE 
  FROM tf_b_trade_plat_register a
 WHERE partition_id = MOD(:TRADE_ID,10000)
   AND trade_id = :TRADE_ID
   and (rsrv_num1 = to_number(:RSRV_NUM1) or :RSRV_NUM1 is null)
   and oper_code IN ('01','03','08','80')
   AND NOT EXISTS(SELECT 1 FROM tf_f_user_plat_register 
                   WHERE partition_id = mod(a.User_Id,10000)
                     AND user_id = a.user_id
                     AND biz_type_code = a.biz_type_code
                     and sysdate between start_date and end_date)