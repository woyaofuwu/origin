SELECT count(1) recordcount
  FROM tf_f_user_svc a , td_b_serv_itemb b
 WHERE a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND a.user_id = TO_NUMBER(:USER_ID)
   AND a.service_id = :SERVICE_ID
   AND a.end_date > SYSDATE
   AND a.serv_para1 = b.item_field_code
   AND a.service_id = b.service_id
   AND b.item_index = TO_NUMBER(:ITEM_INDEX)
   AND b.item_field_code = :ITEM_FIELD_CODE
   AND b.end_date > SYSDATE