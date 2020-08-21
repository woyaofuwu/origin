SELECT COUNT(1) recordcount
  FROM TF_F_CUST_GROUPMEMBER A, TF_F_CUST_GROUP B
 WHERE A.GROUP_ID = B.GROUP_ID
   AND A.USER_ID = :USER_ID
   AND A.REMOVE_TAG = '0'
   AND B.REMOVE_TAG = '0'
   AND B.CLASS_ID IN ('5', '6', '7', '8', '9')
   AND EXISTS(
   SELECT 1 FROM tf_f_relation_uu c
   WHERE c.User_Id_b=:USER_ID
   AND c.relation_type_code=:RELATION_TYPE_CODE
   AND c.end_date>SYSDATE)