select case
         when sysdate > t.ticket_end_date  then
          'EXP'
         else
          'IN'
       end DATE_STATE, t.* from TL_B_USER_COUPONS t where t.serial_number=:SERIAL_NUMBER order by t.update_time desc