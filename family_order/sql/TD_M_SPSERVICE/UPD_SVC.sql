UPDATE td_m_spservice
   SET biz_type=:BIZ_TYPE,biz_desc=:BIZ_DESC,biz_type_code=:BIZ_TYPE_CODE,access_model=:ACCESS_MODEL,price=TO_NUMBER(:PRICE),billing_type=:BILLING_TYPE,biz_status=:BIZ_STATUS,prov_addr=:PROV_ADDR,prov_port=:PROV_PORT,usage_desc=:USAGE_DESC,intro_url=:INTRO_URL,foregift_code=:FOREGIFT_CODE,foregift=TO_NUMBER(:FOREGIFT),remark=:REMARK,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID,update_time=sysdate  
 WHERE sp_id=:SP_ID
   AND sp_svc_id=:SP_SVC_ID