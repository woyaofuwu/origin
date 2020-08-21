UPDATE td_b_siservice_discnt
   SET end_date=sysdate,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID,update_time=sysdate 
 WHERE product_id=:PRODUCT_ID
   AND org_domain=:ORG_DOMAIN
   AND cop_id=:COP_ID
   AND biz_code=:BIZ_CODE
   AND discnt_code=:DISCNT_CODE