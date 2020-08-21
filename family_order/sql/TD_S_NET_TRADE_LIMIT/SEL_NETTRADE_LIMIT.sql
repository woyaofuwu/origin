--IS_CACHE=Y
--IS_CACHE=N
select net_type_code,trade_type_code,limit_tag from TD_S_NET_TRADE_LIMIT 
where 1=1 and TRADE_TYPE_CODE = :TRADE_TYPE_CODE AND net_type_code = :NET_TYPE_CODE and limit_tag =:LIMIT_TAG
AND END_DATE>SYSDATE