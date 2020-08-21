UPDATE td_s_commpara
   SET  param_name=:PARAM_NAME,eparchy_code=:EPARCHY_CODE,end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),remark=:REMARK,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID,update_time=sysdate
 WHERE subsys_code=:SUBSYS_CODE
   AND param_attr=:PARAM_ATTR
   AND param_code=:PARAM_CODE
   AND para_code1=:PARA_CODE1
   AND eparchy_code=:EPARCHY_CODE
   AND start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')