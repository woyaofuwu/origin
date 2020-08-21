UPDATE td_m_design
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),code_state_code=:CODE_STATE_CODE,update_time=sysdate,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID  
 WHERE release_eparchy_code=:RELEASE_EPARCHY_CODE
   AND design_code=:DESIGN_CODE
   AND start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')