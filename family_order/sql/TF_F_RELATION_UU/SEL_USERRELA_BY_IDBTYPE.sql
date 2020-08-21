SELECT *
  FROM tf_f_relation_uu a
 WHERE serial_number_b=:SERIAL_NUMBER_B
   AND sysdate<=end_date+0
   AND start_date+0<=end_date+0
   And (:RELATION_TYPE_CODE is null or relation_type_code=:RELATION_TYPE_CODE)
   And Exists
   (Select 1 From td_s_commpara b Where b.param_attr=2011 
    And a.relation_type_code=b.para_code1
   And b.end_date>Sysdate)