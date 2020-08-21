--IS_CACHE=Y
select serialnumber,trade_type,trade_type_code,key_value,gt_value,oper_type,update_time,
   update_staff_id,update_depart_id,t.remark,rsrv_str1,rsrv_str2,rsrv_str3
 from TD_M_SCP t 
 WHERE serialnumber=SUBSTR(:SERIALNUMBER,0,7)  and trade_type=:TRADE_TYPE and trade_type_code=:TRADE_TYPE_CODE