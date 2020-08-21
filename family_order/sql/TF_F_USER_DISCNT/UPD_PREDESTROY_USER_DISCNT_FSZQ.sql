UPDATE tf_f_user_discnt a
   SET end_date = (CASE  WHEN 
           exists(SELECT RELATION_TYPE_CODE FROM  TF_F_RELATION_UU T WHERE T.USER_ID_B=a.user_id 
            and  partition_id = MOD(TO_NUMBER(t.USER_ID_B),10000)
            and t.user_id_a=a.user_id_a
            and t.end_date>sysdate
            and RELATION_TYPE_CODE in('20','21','22'))  
                      THEN SYSDATE                    
                      ELSE to_date(:EXP_DATE,'YYYY-MM-DD hh24:mi:ss')  END),
       update_time=SYSDATE
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND end_date+0 >= to_date(:EXP_DATE,'YYYY-MM-DD hh24:mi:ss')