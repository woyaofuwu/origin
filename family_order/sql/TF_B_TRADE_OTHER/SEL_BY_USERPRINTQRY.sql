select 
b.serial_number,a.trade_id,a.rsrv_value_code,a.rsrv_value,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str6,a.rsrv_str5,a.rsrv_str4,a.rsrv_str8,a.rsrv_str9,a.rsrv_str10,a.start_date,a.end_date,a.modify_tag,b.trade_staff_id,a.rsrv_str7 
from tf_b_trade_other a,tf_bh_trade b 
where a.trade_id=b.trade_id 
and (a.rsrv_value_code=:RSRV_VALUE_CODE or :RSRV_VALUE_CODE is null)
and (b.city_code=:CITY_CODE or :CITY_CODE is null)
and b.accept_date between TO_DATE(:START_DATE,'YYYY-MM-DD') and TO_DATE(:END_DATE,'YYYY-MM-DD')
and b.trade_type_code='332'
AND b.accept_month = to_number(substr(to_char(TO_DATE(:START_DATE,'YYYY-MM-DD'),'yyyymmdd'),5,2))
order by b.accept_date