select a.*
from tf_bh_trade a,tf_b_trade_svc b
where a.user_id = :USER_ID
and b.modify_tag = '0'
and SYSDATE between b.start_date and b.End_Date
and EXISTS (select 1 from td_s_commpara c 
    where b.service_id = c.para_code1
    and c.subsys_code = 'CSM'
    and c.param_attr = 162
    and c.param_code = :TRADE_TYPE_CODE
    and (c.eparchy_code = :EPARCHY_CODE or c.eparchy_code = 'ZZZZ'))
and a.trade_id = b.trade_id
and a.eparchy_code = :EPARCHY_CODE
and a.cancel_tag = '0'
and a.accept_date >to_date(:ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss')