UPDATE td_s_commpara
   SET para_code3=:PARA_CODE3,para_code4=:PARA_CODE4,para_code5=:PARA_CODE5,para_code6=:PARA_CODE6,para_code7=:PARA_CODE7,para_code8=:PARA_CODE8,para_code9=:PARA_CODE9,para_code10=:PARA_CODE10,para_code20=:PARA_CODE20,end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),eparchy_code=:EPARCHY_CODE,remark=:REMARK,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID,update_time=TO_DATE(:UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE subsys_code=:SUBSYS_CODE
   AND param_attr=:PARAM_ATTR
   AND param_code=:PARAM_CODE
   AND para_code1=:PARA_CODE1
   AND para_code2=:PARA_CODE2