SELECT * FROM TF_F_USER_ALTSN where SERIAL_NUMBER = :SERIAL_NUMBER and STATUS = :STATUS
                               and  RELA_TYPE = :RELA_TYPE and ACTIVATE_TIME > ( add_months ( sysdate , :INTERVAL ) ) 
                               and EXPIRE_DEAL_TAG is null