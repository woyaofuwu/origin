update TF_B_TRADE_SVC t
set t.START_DATE=to_date(:FINISH_DATE,'yyyy-mm-dd hh24:mi:ss')
WHERE T.TRADE_ID=:TRADE_ID
and sysdate between t.start_date and t.end_date