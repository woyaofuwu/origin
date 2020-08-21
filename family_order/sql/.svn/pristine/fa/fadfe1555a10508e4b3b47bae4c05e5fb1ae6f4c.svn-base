select *
from TF_F_USER_SALEACTIVE_BOOK a
where a.book_type = '0'
And a.serial_number=:SERIAL_NUMBER
And a.accept_date Between To_Date(:START_DATE, 'yyyy-mm-dd') And To_Date(:END_DATE||' 23:59:59', 'yyyy-mm-dd hh24:mi:ss')
And a.trade_staff_id=:TRADE_STAFF_ID
And a.relation_trade_id=:TRADE_ID
And a.deal_state_code=:DEAL_STATE_CODE
order by a.accept_date desc