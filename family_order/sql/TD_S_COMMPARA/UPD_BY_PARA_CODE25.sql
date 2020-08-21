UPDATE td_s_commpara
   SET param_name=:PARAM_NAME,para_code1=:PARA_CODE1,para_code3=:PARA_CODE3,start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'),end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),remark=:REMARK,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID,update_time=sysdate  
 WHERE para_code25=:PARA_CODE25