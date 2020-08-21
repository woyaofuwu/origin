SELECT a.partition_id,to_char(a.trade_id) trade_id,to_char(user_id) user_id,serial_number,biz_code,sp_code,product_no,
       biz_type_code,org_domain,opr_source,biz_state_code,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(first_date,'yyyy-mm-dd hh24:mi:ss') first_date,
       to_char(first_date_mon,'yyyy-mm-dd hh24:mi:ss') first_date_mon,gift_serial_number,gift_user_id,bill_type,
       price,to_char(subscribe_id) subscribe_id,rsrv_num1,rsrv_num2,rsrv_num3,to_char(rsrv_num4) rsrv_num4,
       to_char(rsrv_num5) rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,
       rsrv_str9,rsrv_str10,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,
       to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,
       to_char(rsrv_date4,'yyyy-mm-dd hh24:mi:ss') rsrv_date4,to_char(rsrv_date5,'yyyy-mm-dd hh24:mi:ss') rsrv_date5,
       remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,oper_code,
       b.id,b.id_type,b.discnt_code,b.modify_tag,b.id_a,b.accept_month
  FROM tf_b_trade_plat_order a,tf_b_trade_discnt b
 WHERE a.partition_id = MOD(TO_NUMBER(:TRADE_ID),10000)
   AND a.trade_id = :TRADE_ID
   AND a.TRADE_ID = b.TRADE_ID(+)
   AND a.USER_ID = b.ID(+)
   AND a.rsrv_num1=:RSRV_NUM1