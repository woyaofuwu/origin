INSERT INTO tf_f_user_discnt(partition_id,user_id,user_id_a,product_id,package_id,discnt_code,inst_id,spec_tag,relation_type_code,start_date,end_date,update_time)
 SELECT :PARTITION_ID,TO_NUMBER(:USER_ID),TO_NUMBER(:USER_ID_A),:PRODUCT_ID,:PACKAGE_ID,:DISCNT_CODE,:INST_ID,:SPEC_TAG,:RELATION_TYPE_CODE,TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'),TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS'),sysdate
  FROM dual
 WHERE  NOT EXISTS (SELECT 1 FROM tf_f_user_discnt
                    WHERE user_id = TO_NUMBER(:USER_ID)
                      AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
                      AND discnt_code = TO_NUMBER(:DISCNT_CODE)
                      AND end_date > start_date
                      AND product_id=to_number(:PRODUCT_ID) 
                      And package_id=to_number(:PACKAGE_ID)
                      And user_id_a=TO_NUMBER(:USER_ID_A)
                      and start_date = to_date(:START_DATE,'yyyy-mm-dd hh24:mi:ss'))