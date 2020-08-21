SELECT SERIAL_NUMBER_B,SERIAL_NUMBER_A,uu.USER_ID_A , uu.user_id_b,r.RELATION_TYPE_CODE,r.RELATION_TYPE_NAME,ROLE_CODE_A,ROLE_CODE_B,SHORT_CODE,START_DATE,END_DATE
      FROM tf_f_relation_uu uu,td_s_relation r
      WHERE uu.relation_type_code=r.relation_type_code
      AND SERIAL_NUMBER_A=:SERIAL_NUMBER_A
      AND SERIAL_NUMBER_B=:SERIAL_NUMBER_B
      AND uu.relation_type_code = :RELATION_TYPE_CODE
        AND sysdate BETWEEN start_date AND end_date
         AND end_date>last_day(trunc(sysdate))+1-1/24/3600