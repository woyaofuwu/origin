INSERT INTO tf_f_user_mbmp_sub(partition_id,user_id,sp_id,biz_type_code,org_domain,opr_source,sp_svc_id,start_date,end_date,biz_state_code,billflg,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_num4,rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_date1,rsrv_date2,rsrv_date3,remark,update_staff_id,update_depart_id,update_time,serial_number)

select a.partition_id,a.user_id,a.sp_id,a.biz_type_code,a.org_domain,a.opr_source,a.sp_svc_id,sysdate,to_date('20501231','yyyymmdd'),:BIZ_STATE_CODE,a.billflg,a.rsrv_num1,a.rsrv_num2,a.rsrv_num3,a.rsrv_num4,a.rsrv_num5,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,a.rsrv_date1,a.rsrv_date2,a.rsrv_date3,'改号更新',a.update_staff_id,a.update_depart_id,a.update_time,:SERIAL_NUMBER 

  from tf_f_user_mbmp_sub a
 WHERE a.user_id=TO_NUMBER(:USER_ID)
   AND a.biz_type_code=:BIZ_TYPE_CODE
   AND a.biz_state_code='E'
   AND a.end_date=(SELECT max(end_date)FROM tf_f_user_mbmp_sub WHERE user_id=:USER_ID AND biz_type_code=:BIZ_TYPE_CODE)