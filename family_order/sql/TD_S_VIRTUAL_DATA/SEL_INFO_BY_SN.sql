select t.id_type,t.serial_number,t.transaction_id,t.pay_transid,
t.action_date,t.action_time,t.virt_flow,t.organ_id,t.cnl_type,
t.payed_type,t.settle_date,t.payment,t.order_no,t.order_cnt,t.commission
 from TD_S_VIRTUAL_DATA t
 where t.serial_number=:SERIAL_NUMBER
 and to_char(t.action_time,'yyyy-mm-dd hh:mm:ss')
 between to_char(trunc(add_months(last_day(to_date(:START_DATE, 'yyyy-mm')), -1) + 1), 'yyyy-mm-dd hh:mm:ss') and
 to_char(last_day(to_date(:END_DATE, 'yyyy-mm')), 'yyyy-mm-dd hh:mm:ss')