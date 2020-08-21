SELECT max(to_number(OUT_TIME - IN_TIME) * 24), STAFF_ID
  from TL_M_STAFFLOG
 where to_char(in_time, 'YYYY-MM-DD') = :DATE
   and staff_id = :STAFF_ID
 group by STAFF_ID
Having max(to_number(OUT_TIME - IN_TIME) * 24) > to_number(:TIME)