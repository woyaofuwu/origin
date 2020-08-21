 SELECT  a.*  
   FROM tf_b_trade a
   WHERE 1=1
    AND a.TRADE_ID = :TRADE_ID
    AND a.IN_MODE_CODE = :IN_MODE_CODE
    AND a.SERIAL_NUMBER = :SERIAL_NUMBER
    AND a.cancel_tag = '0'