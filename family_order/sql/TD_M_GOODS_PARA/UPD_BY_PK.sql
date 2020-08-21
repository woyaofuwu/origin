UPDATE td_m_goods_para
   SET para_name=:PARA_NAME,end_date=TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS'),remark=:REMARK,update_time=sysdate,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID  
 WHERE eparchy_code=:EPARCHY_CODE
   AND para_code1=:PARA_CODE1
   AND para_code2=:PARA_CODE2