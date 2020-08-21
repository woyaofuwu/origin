select substr( max( to_char( ACCEPT_DATE, 'yyyymmddhh24miss' )||to_char( user_id ) ), 15 ) user_id
from tf_b_trade
where serial_number = :SERIAL_NUMBERR
      and trade_type_code = :TRADE_TYPE_CODE