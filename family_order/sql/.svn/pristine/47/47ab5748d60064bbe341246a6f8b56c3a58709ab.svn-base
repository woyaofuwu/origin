select serial_number_b from (
select serial_number_b,rownum row_num from (
SELECT to_char(serial_number_b) serial_number_b
  FROM tf_f_relation_uu
 WHERE user_id_a = TO_NUMBER(:USER_ID_A)
   AND relation_type_code = :RELATION_TYPE_CODE
   AND sysdate < end_date+0
   group BY serial_number_b
)
) where row_num>TO_NUMBER(:PAGE_NUM)*5000 and row_num<(TO_NUMBER(:PAGE_NUM)+1)*5000