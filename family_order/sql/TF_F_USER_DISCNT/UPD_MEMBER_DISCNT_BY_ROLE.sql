UPDATE tf_f_user_discnt a
   SET end_date=TRUNC(sysdate) - 1/24/3600  
 WHERE partition_id=:PARTITION_ID
   AND user_id=TO_NUMBER(:USER_ID)
   AND user_id_a=TO_NUMBER(:USER_ID_A)
   AND relation_type_code=:RELATION_TYPE_CODE
   AND end_date > sysdate
   AND EXISTS (SELECT 1 FROM td_s_commpara 
               WHERE subsys_code = 'CSM'
                 AND param_attr = 6018
                 AND param_code = to_number(a.discnt_code )
                 AND sysdate between start_date and end_date
              )