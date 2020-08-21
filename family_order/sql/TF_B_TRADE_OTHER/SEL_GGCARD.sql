select to_char(a.trade_id) trade_id,a.rsrv_value_code,a.rsrv_value,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,a.rsrv_str6,a.rsrv_str7,a.rsrv_str8,a.rsrv_str9,a.rsrv_str10,a.modify_tag,to_char(a.start_date,'YYYY-MM-DD HH24:MI:SS') start_date,to_char(a.end_date,'YYYY-MM-DD HH24:MI:SS') end_date,b.serial_number 
from tf_b_trade_other a,tf_bh_trade b
where a.trade_id=b.trade_id
and a.rsrv_value_code='GGTH'
and b.trade_type_code=1220
and (b.trade_eparchy_code=:TRADE_EPARCHY_CODE or :TRADE_EPARCHY_CODE is null)
and (:RSRV_STR1 is null or a.rsrv_str1=:RSRV_STR1)
and (:RSRV_STR2 is null or a.rsrv_str2=:RSRV_STR2)
and (:RSRV_STR3 is null or a.rsrv_str3=:RSRV_STR3)
AND b.accept_date>=TRUNC(TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'))
AND b.accept_date<=TRUNC(TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'))
and (:SERIAL_NUMBER is null or b.serial_number=:SERIAL_NUMBER)
and (:CARD_NO is null or a.rsrv_value=:CARD_NO)