SELECT partition_id,to_char(user_id) user_id,sp_id,biz_type_code,org_domain,opr_source,sp_svc_id,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,biz_state_code,billflg,rsrv_num1,rsrv_num2,rsrv_num3,to_char(rsrv_num4) rsrv_num4,to_char(rsrv_num5) rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,serial_number 

  FROM tf_f_user_mbmp_sub

 WHERE user_id=TO_NUMBER(:USER_ID)

   AND sp_id=:SP_ID

   AND biz_type_code=:BIZ_TYPE_CODE

   AND org_domain=:ORG_DOMAIN

   AND (sp_svc_id=:SP_SVC_ID OR :SP_SVC_ID = '*')

   AND start_date < SYSDATE 

   AND end_date > SYSDATE

   AND (biz_state_code=:BIZ_STATE_CODE OR :BIZ_STATE_CODE is NULL)