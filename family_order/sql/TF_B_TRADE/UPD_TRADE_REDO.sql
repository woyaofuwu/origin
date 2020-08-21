update tf_b_trade a set a.SUBSCRIBE_STATE = 'D' 
where trade_id in(
select trade_id from TL_BPM_ERROR_LOG B 
where B.ERR_TIME>=(SYSDATE)-:INDAYS
AND B.DEAL_TAG='0' 
and B.RSRV_STR1<=10 
)