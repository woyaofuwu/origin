select user_id,trade_id,accept_month,cancel_tag,stand_address,detail_address,sign_path,port_type,secret,stand_address_code,
       old_stand_address_code,old_stand_address,old_detail_address,
       modify_tag,inst_id,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,
       to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,remark,rsrv_num1,rsrv_num2,rsrv_num3,
       rsrv_num4,rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_date1,rsrv_date2,rsrv_date3,rsrv_tag1,rsrv_tag2,rsrv_tag3
       from TF_B_TRADE_TELEPHONE
       where trade_id = :TRADE_ID and accept_month = to_number(substr(:TRADE_ID,5,2))