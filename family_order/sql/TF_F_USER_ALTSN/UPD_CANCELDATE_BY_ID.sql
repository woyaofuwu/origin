update TF_F_USER_ALTSN set ALT_CANCEL_TIME = to_date ( :ALT_CANCEL_TIME , 'yyyy-mm-dd hh24:mi:ss' ), status = :TO_STATUS where serial_number = :SERIAL_NUMBER and status = :FROM_STATUS and RELA_TYPE = :RELA_TYPE