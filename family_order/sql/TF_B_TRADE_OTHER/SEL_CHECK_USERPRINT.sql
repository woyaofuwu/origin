select to_char(a.trade_id) trade_id,a.rsrv_value_code,a.rsrv_value,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,a.rsrv_str6,a.rsrv_str7,a.rsrv_str8,a.rsrv_str9,a.rsrv_str10,a.modify_tag,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date
 from tf_b_trade_other a,tf_bh_trade b 
where 
to_number(a.rsrv_str7)=b.trade_id 
and a.rsrv_str7=:TRADE_ID
and a.rsrv_value_code=:RSRV_VALUE_CODE 
and b.trade_type_code='330'
and a.rsrv_value_code in ('801','803','804','805','807','808','810','811','812','813')