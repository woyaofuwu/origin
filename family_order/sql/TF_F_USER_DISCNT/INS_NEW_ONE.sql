INSERT INTO TF_F_USER_DISCNT(partition_id,user_id,user_id_a,product_id,package_id,discnt_code,spec_tag,relation_type_code,inst_id,
    campn_id,start_date,end_date,update_time,update_staff_id,update_depart_id,remark)
 SELECT MOD(TO_NUMBER(:USER_ID),10000),TO_NUMBER(:USER_ID),TO_NUMBER(:USER_ID_A),:PRODUCT_ID,:PACKAGE_ID,:DISCNT_CODE,
    NVL(:SPEC_TAG,'0'),:RELATION_TYPE_CODE,:INST_ID,:CAMPN_ID,
    TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'),TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS'),
    sysdate,:UPDATE_STAFF_ID,:UPDATE_DEPART_ID,:REMARK
  FROM dual
  WHERE NOT EXISTS (SELECT 1 FROM tf_f_user_discnt
                WHERE user_id = to_number(:USER_ID)
                  AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
                  AND product_id = :PRODUCT_ID
                  AND package_id = :PACKAGE_ID
                  AND user_id_a = TO_NUMBER(:USER_ID_A)
                  AND discnt_code= :DISCNT_CODE
                  AND end_date>start_date
                  AND end_date>TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'))