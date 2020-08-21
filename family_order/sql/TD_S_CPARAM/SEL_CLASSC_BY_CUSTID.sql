SELECT COUNT(1) recordcount
  FROM TF_F_CUST_GROUPMEMBER A, TF_F_CUST_GROUP B
 WHERE A.GROUP_ID = B.GROUP_ID
   AND A.USER_ID = :USER_ID
   AND A.REMOVE_TAG = '0'
   AND B.REMOVE_TAG = '0'
   AND B.CLASS_ID = :CLASS_ID
   AND B.CUST_ID = :CUST_ID
   AND NOT EXISTS(
   SELECT 1 FROM tf_f_user_discnt c
   WHERE c.user_id=:USER_ID
   AND   c.discnt_code=:DISCNT_CODE
   AND   c.end_date>SYSDATE)
   AND EXISTS(
   SELECT 1 FROM tf_f_relation_uu c
   WHERE c.User_Id_b=:USER_ID
   AND c.relation_type_code=:RELATION_TYPE_CODE
   AND c.end_date>SYSDATE)