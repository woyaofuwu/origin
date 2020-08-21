UPDATE td_m_design
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),res_type_code=:RES_TYPE_CODE,res_kind_code=:RES_KIND_CODE,capacity_type_code=:CAPACITY_TYPE_CODE,start_res_no=:START_RES_NO,end_res_no=:END_RES_NO,design_name=:DESIGN_NAME,code_state_code=:CODE_STATE_CODE,release_time=TO_DATE(:RELEASE_TIME, 'YYYY-MM-DD HH24:MI:SS'),release_staff_id=:RELEASE_STAFF_ID,release_depart_id=:RELEASE_DEPART_ID,remark=:REMARK,rsvalue2=:RSVALUE2,update_time=sysdate,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID  
 WHERE release_eparchy_code=:RELEASE_EPARCHY_CODE
   AND design_code=:DESIGN_CODE
   AND start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')