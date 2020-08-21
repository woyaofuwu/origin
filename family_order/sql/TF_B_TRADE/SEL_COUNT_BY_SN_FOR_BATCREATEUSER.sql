SELECT trade_id
          FROM tf_b_trade
         WHERE (trade_id) =
               (SELECT MAX(trade_id)
                  FROM tf_b_trade
                 WHERE trade_type_code IN (500,10)
                   AND serial_number = :SERIAL_NUMBER)