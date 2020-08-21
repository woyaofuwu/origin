select 
  a.trade_staff_id, 
  a.trade_type_code, 
  a.total,
  b.PARA_CODE1 PDATA_ID,
  b.PARAM_NAME DATA_NAME
from (
   select trade_staff_id, trade_type_code, count(*) total from tf_bh_trade  
   where to_char(accept_date, 'YYYY-MM-DD') = :DATE
     and accept_month = to_number (to_char (accept_date, 'MM'))
     and cancel_tag = '0'
   group by trade_staff_id, trade_type_code order by trade_staff_id) a,
   td_s_commpara b
where a.trade_type_code = b.param_code
  and a.total > to_number(b.para_code1)
  and b.param_attr = '9984'
  and b.subsys_code = 'CSM'