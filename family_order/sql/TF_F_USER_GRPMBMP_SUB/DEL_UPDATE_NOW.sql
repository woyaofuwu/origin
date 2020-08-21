UPDATE tf_f_user_grpmbmp_sub a

   SET a.end_date=(SELECT end_date FROM tf_b_trade_grpmbmp_sub

                  WHERE trade_id = :TRADE_ID

                    AND user_id = TO_NUMBER(a.user_id)

                    and ec_user_id = TO_NUMBER(a.ec_user_id)

                    and biz_code = a.biz_code

                    and sysdate between a.start_date and a.end_date

                    and modify_tag <> '0')

 WHERE EXISTS (SELECT 1 FROM tf_b_trade_grpmbmp_sub 

                WHERE user_id = a.user_id

                 AND biz_code=a.biz_code

                 and ec_user_id = a.ec_user_id

                 AND sysdate between a.start_date and a.end_date

                 and trade_id = :TRADE_ID

                 and modify_tag <> '0')

AND a.serial_number = (

      SELECT serial_number FROM tf_b_trade_grpmbmp_sub 

      WHERE  trade_id = :TRADE_ID

  )