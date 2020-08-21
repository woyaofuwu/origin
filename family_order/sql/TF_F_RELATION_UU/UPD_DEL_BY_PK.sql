UPDATE Tf_f_Relation_uu
   SET end_date = to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss'),update_time=sysdate
  WHERE user_id_a = to_number(:USER_ID_A)
    AND user_id_b = to_number(:USER_ID_B)
    AND partition_id = MOD(to_number(:USER_ID_B),10000)
    AND relation_type_code = :RELATION_TYPE_CODE
    AND start_date = to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss')