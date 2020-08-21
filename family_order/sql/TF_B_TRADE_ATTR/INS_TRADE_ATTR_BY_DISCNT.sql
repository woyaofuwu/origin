INSERT INTO tf_b_trade_attr(TRADE_ID,Accept_Month,User_Id,inst_type,inst_id,attr_code,attr_value,start_date,end_date,modify_tag,update_time
,update_staff_id,update_depart_id)
select t.trade_id,t.accept_month,t.user_id,'D',t.inst_id,:ATTR_CODE,:ATTR_VALUE,t.start_date,t.end_date,t.modify_tag,t.update_time,
t.update_staff_id,t.update_depart_id from tf_b_trade_discnt t
where t.trade_id=:TRADE_ID and t.discnt_code=:DISCNT_CODE and t.user_id=:USER_ID