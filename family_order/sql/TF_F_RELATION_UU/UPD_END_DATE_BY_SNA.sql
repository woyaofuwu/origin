UPDATE tf_f_relation_uu
   SET end_date=sysdate,
    REMARK=:REMARK
 WHERE serial_number_a=:SERIAL_NUMBER_A
   AND relation_type_code=:RELATION_TYPE_CODE
   AND end_date>sysdate