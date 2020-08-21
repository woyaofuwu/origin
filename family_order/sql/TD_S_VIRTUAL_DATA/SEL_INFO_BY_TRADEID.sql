select t.id_type,t.serial_number,t.transaction_id,t.pay_transid,
t.action_date,t.action_time,t.virt_flow,t.organ_id,t.cnl_type,
t.payed_type,t.settle_date,t.payment,t.order_no,t.order_cnt,t.commission
 from TD_S_VIRTUAL_DATA t
 where t.TRANSACTION_ID=:trade_id