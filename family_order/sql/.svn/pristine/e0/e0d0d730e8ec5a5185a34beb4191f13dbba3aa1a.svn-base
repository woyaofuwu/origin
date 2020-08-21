select t.*
  from tf_f_relation_uu T
 WHERE T.USER_ID_A IN
       (select t.USER_ID_A
          from tf_f_relation_uu t
         where t.serial_number_b = :MAIN_SERIAL_NUMBER
           and t.relation_type_code = 'CP'
           AND T.ROLE_CODE_B = '1'
           AND SYSDATE BETWEEN T.START_DATE AND T.END_DATE)
			AND T.ROLE_CODE_B='2'
			AND SYSDATE BETWEEN T.START_DATE AND T.END_DATE