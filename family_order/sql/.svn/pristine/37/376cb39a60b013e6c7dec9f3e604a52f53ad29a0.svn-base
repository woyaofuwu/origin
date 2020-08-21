SELECT log_id,error_num - :LIMIT_TIMES as disTimes ,t.card_pwd
      FROM TF_F_PAYMEMNT_SURELY t
     WHERE MAIN_SERIAL_NUMBER = :SERIAL_NUMBER
       and SURELY_SERIAL_NUMBER = :X_CALL_EDMPHONECODE
       and sysdate between start_date and end_date