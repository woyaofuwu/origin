--IS_CACHE=Y
select DISTINCT t.trade_type_code,t.trade_type,t.end_date from td_s_tradetype t 
where  t.trade_type_code like '%'||:TRADE_TYPE_CODE||'%'
and t.trade_type like  '%'||:TRADE_TYPE||'%'
and sysdate between start_date and end_date