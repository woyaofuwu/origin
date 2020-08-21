select score,user_id,year_id,cycle_id,id_type,score_type_code,
(select score from tf_f_user_newscore  where user_id = :USER_ID and year_id = '1000') rsrv_num1
 from tf_f_user_newscore
 where  user_id = :USER_ID  
     and partition_id=MOD(TO_NUMBER(:USER_ID),10000)
     and (year_id = :YEAR_ID  OR :YEAR_ID = '-1')  
     and (id_type = :ID_TYPE OR :ID_TYPE = 'Z')
     and (score_type_code = :SCORE_TYPE_CODE or :SCORE_TYPE_CODE = '-1')