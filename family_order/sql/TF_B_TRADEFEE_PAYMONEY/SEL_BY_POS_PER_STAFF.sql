select c.update_staff_id,d.pos/c.total per
     from           
        (select a.update_staff_id update_staff_id, count(*) total
          from tf_b_tradefee_paymoney a
         where to_char(a.update_time, 'YYYY-MM-DD') = :DATE
          and update_staff_id=:STAFF_ID
           and accept_month = to_number (to_char (update_time, 'MM'))
         group by update_staff_id
         ) c,
         (select b.update_staff_id, count(*) pos
          from tf_b_tradefee_paymoney b
          where b.pay_money_code = 'P'
          and to_char(b.update_time, 'YYYY-MM-DD') = :DATE
           and update_staff_id=:STAFF_ID
          and accept_month = to_number (to_char (update_time, 'MM'))
          group by update_staff_id) d
          where c.update_staff_id=d.update_staff_id
          and d.pos/c.total > :VALUE