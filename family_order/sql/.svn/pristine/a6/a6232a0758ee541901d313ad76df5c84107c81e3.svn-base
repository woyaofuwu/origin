UPDATE td_b_siservice_discnt
   SET discnt_name=:DISCNT_NAME,discnt_mode=:DISCNT_MODE,force_tag=:FORCE_TAG,rela_discnt_code=:RELA_DISCNT_CODE,start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'),end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID,update_time=sysdate 
 WHERE product_id=:PRODUCT_ID
   AND org_domain=:ORG_DOMAIN
   AND cop_id=:COP_ID
   AND biz_code=:BIZ_CODE
   AND discnt_code=:DISCNT_CODE