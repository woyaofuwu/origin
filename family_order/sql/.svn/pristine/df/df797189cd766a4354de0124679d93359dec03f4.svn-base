SELECT log_id,supply_num - :LIMIT_TIMES as DISTIMES ,remind_type,remind_content
      FROM TF_F_SUPPLYPAY_INSPECT  
     WHERE MAIN_SERIAL_NUMBER = :SERIAL_NUMBER
       and SURELY_SERIAL_NUMBER = :X_CALL_EDMPHONECODE
       and eparchy_code = :EPARCHY_CODE
       and sysdate between start_date and end_date