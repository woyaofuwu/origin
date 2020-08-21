UPDATE td_o_credit_usergroup
   SET name=:NAME,remark=:REMARK,update_time=SYSDATE,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID  
 WHERE user_group_id=:USER_GROUP_ID