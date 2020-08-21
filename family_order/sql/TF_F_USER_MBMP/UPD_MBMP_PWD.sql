UPDATE tf_f_user_mbmp
   SET passwd=:PASSWD,remark=:REMARK,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID,update_time=sysdate 
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND biz_type_code=:BIZ_TYPE_CODE
   AND org_domain=:ORG_DOMAIN
   AND end_date>sysdate