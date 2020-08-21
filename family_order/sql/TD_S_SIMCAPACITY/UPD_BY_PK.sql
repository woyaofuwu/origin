UPDATE td_s_simcapacity
   SET 
capacity_type=:CAPACITY_TYPE,remark=:REMARK,update_time=SYSDATE,
update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID  
 WHERE eparchy_code=:EPARCHY_CODE
   AND capacity_type_code=:CAPACITY_TYPE_CODE