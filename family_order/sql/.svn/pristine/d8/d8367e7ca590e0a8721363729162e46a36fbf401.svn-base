select decode(a.TRADE_TYPE_CODE,'295','开通','297','调整','298','取消','110','取消','100','取消','其他') RSRV_STR7,c.in_mode,d.pay_money_code,decode(b.RSRV_STR4,'0','普通有价值客户','1','特殊身份客户','2','被担保客户','其他') RSRV_STR4,a.accept_date START_DATE,b.RSRV_STR1,b.RSRV_STR2,b.RSRV_STR3,a.remark,a.trade_staff_id,a.rsrv_str4 CUST_MANAGER_ID,a.rsrv_str5 ACTOR_NAME,a.rsrv_str6 ACTOR_PHONE
from tf_bh_trade a,tf_b_trade_other b,td_s_inmode c,tf_b_tradefee_paymoney d
where a.trade_type_code in('295','297','298','110','100')
and a.serial_number = to_char(:SERIAL_NUMBER)
and a.trade_id = b.trade_id
and b.rsrv_value_code = 'CRED'
and a.trade_id = d.trade_id(+)
and a.in_mode_code = c.in_mode_code
and c.eparchy_code =  a.trade_eparchy_code
and trunc(b.start_date) BETWEEN to_date(:START_DATE,'YYYYMMDD') AND to_date(:END_DATE,'YYYYMMDD')
order by 5