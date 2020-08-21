SELECT to_char(log_id) log_id,in_date,city_code,stock_id,staff_id,res_kind_code,capacity_type_code,eparchy_code,oper_type_code,stock_level,in_tag,modify_tag,fee,to_char(total_num) total_num,to_char(apply_num) apply_num,to_char(cancel_num) cancel_num,to_char(oper_num) oper_num,oper_staff_id,oper_depart_id,to_char(oper_time,'yyyy-mm-dd hh24:mi:ss') oper_time,remark,rsrv_tag1,rsrv_tag2,rsrv_tag3,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,nvl(rsrv_num1,0) rsrv_num1,nvl(rsrv_num2,0) rsrv_num2,nvl(rsrv_num3,0) rsrv_num3,nvl(open_num1,0) open_num1,nvl(open_num2,0) open_num2,nvl(open_num3,0) open_num3
  FROM ts_a_goods_dif
 WHERE in_date>=:IN_DATE_S and in_date<=:IN_DATE_E
   AND (:CITY_CODE is null or city_code=:CITY_CODE)
   AND (:STOCK_ID is null or stock_id=:STOCK_ID)
   AND (:STAFF_ID is null or staff_id=:STAFF_ID)
   AND (:EPARCHY_CODE is null or eparchy_code=:EPARCHY_CODE)
   AND (:RSRV_STR2 is null or rsrv_str2=:RSRV_STR2)