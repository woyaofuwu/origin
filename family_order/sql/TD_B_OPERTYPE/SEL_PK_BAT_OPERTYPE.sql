--IS_CACHE=Y
SELECT batch_oper_type,batch_oper_name,limit_quantity,priority,field_name1,field_name2,field_name3,field_name4,field_name5,field_name6,field_name7,field_name8,field_name9,field_name10,field_name11,field_name12,field_name13,field_name14,field_name15,field_name16,field_name17,field_name18,field_name19,field_name20,field_name21,field_name22,field_name23,field_name24,field_name25,field_name26,field_name27,field_name28,field_name29,field_name30,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,trade_type_code,tag_set,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5 
  FROM td_b_opertype
 WHERE batch_oper_type=:BATCH_OPER_TYPE
   AND sysdate between start_date and end_date
   AND (eparchy_code=:EPARCHY_CODE or eparchy_code='ZZZZ')