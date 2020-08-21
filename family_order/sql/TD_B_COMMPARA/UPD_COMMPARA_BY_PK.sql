UPDATE td_b_commpara

   SET para_attr=:PARA_ATTR,para_name=:PARA_NAME,para_code1=:PARA_CODE1,para_code2=:PARA_CODE2,para_code3=:PARA_CODE3,para_code4=:PARA_CODE4,para_code5=:PARA_CODE5,para_code6=:PARA_CODE6,para_date7=TO_DATE(:PARA_DATE7, 'YYYY-MM-DD HH24:MI:SS'),para_date8=TO_DATE(:PARA_DATE8, 'YYYY-MM-DD HH24:MI:SS'),para_date9=TO_DATE(:PARA_DATE9, 'YYYY-MM-DD HH24:MI:SS'),para_date10=TO_DATE(:PARA_DATE10, 'YYYY-MM-DD HH24:MI:SS'),start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'),end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),use_tag=:USE_TAG,remark=:REMARK,update_time=decode(:UPDATE_TIME,null,sysdate,TO_DATE(:UPDATE_TIME, 'YYYY-MM-DD HH24:MI:SS')),update_eparchy_code=:UPDATE_EPARCHY_CODE,update_city_code=:UPDATE_CITY_CODE,update_depart_id=:UPDATE_DEPART_ID,update_staff_id=:UPDATE_STAFF_ID  

 WHERE eparchy_code=:EPARCHY_CODE

   AND para_code=:PARA_CODE