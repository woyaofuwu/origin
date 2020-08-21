select case
               when add_months(sysdate, -12) - max(a.destroy_time)  > 0 then
                1
               else
                0
             end recordcount 
        from tf_F_user a 
       where a.SERIAL_NUMBER = :SERIAL_NUMBER
         and a.remove_tag in ('2', '4')