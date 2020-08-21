DELETE FROM tf_f_vpmn_group_member
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND vpmn_group_id=:VPMN_GROUP_ID