UPDATE tf_f_vpmn_group_member
   SET end_date=sysdate  
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND vpmn_group_id=:VPMN_GROUP_ID
   AND member_user_id=TO_NUMBER(:MEMBER_USER_ID)
   and end_date>sysdate