SELECT partition_id,to_char(user_id) user_id,sp_id,biz_type_code,org_domain,opr_source,sp_svc_id,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,biz_state_code,billflg,rsrv_num1,rsrv_num2,rsrv_num3,to_char(rsrv_num4) rsrv_num4,to_char(rsrv_num5) rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,serial_number,bipcode,rsrv_str11,rsrv_str12,rsrv_str13,rsrv_str14,rsrv_str15,rsrv_str16,rsrv_str17,rsrv_str18,rsrv_str19,rsrv_str20,to_char(first_date,'yyyy-mm-dd hh24:mi:ss') first_date 
  FROM TF_F_USER_MBMP_SUB
WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND sp_id=:SP_ID
   AND biz_type_code=:BIZ_TYPE_CODE
   AND org_domain=:ORG_DOMAIN
   AND sp_svc_id=:SP_SVC_ID
   AND rsrv_str5='06'
   AND start_date = (SELECT min(start_date)
                       FROM tf_f_user_mbmp_sub
                      WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
                        AND user_id=TO_NUMBER(:USER_ID)
                        AND sp_id=:SP_ID
                        AND biz_type_code=:BIZ_TYPE_CODE
                        AND org_domain=:ORG_DOMAIN
                        AND sp_svc_id=:SP_SVC_ID
                        AND (biz_state_code = :BIZ_STATE_CODE OR :BIZ_STATE_CODE is NULL)
                        AND start_date between TRUNC(SYSDATE,'mm') and TRUNC(ADD_MONTHS(SYSDATE,1), 'mm') - 1/24/3600
                        AND rsrv_str5='06')