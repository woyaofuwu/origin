select t.serial_number,
       t.section_id,
       max(t.send_date) LAST_SEND_TIME,
       count(1) SEND_COUNT
  from TL_M_IVRSENDMAIL_LOG t
 where 1 = 1
   and t.SERIAL_NUMBER = :SERIAL_NUMBER
   and t.SECTION_ID = :SECTION_ID
   and t.send_date >= to_date(:START_DATE, 'yyyy-mm-dd hh24:mi:ss')
   and t.send_date <= to_date(:END_DATE, 'yyyy-mm-dd hh24:mi:ss')
 group by t.serial_number, t.section_id