INSERT INTO tf_f_user_mbmp_sub(partition_id,user_id,sp_id,biz_type_code,org_domain,opr_source,sp_svc_id,start_date,end_date,biz_state_code,billflg,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_num4,rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str11,rsrv_str12,rsrv_str13,rsrv_date1,rsrv_date2,rsrv_date3,remark,update_staff_id,update_depart_id,update_time,serial_number)

select a.partition_id,a.user_id,a.sp_id,a.biz_type_code,a.org_domain,a.opr_source,a.sp_svc_id,sysdate,to_date('20501231','yyyymmdd'),:BIZ_STATE_CODE1,a.billflg,a.rsrv_num1,a.rsrv_num2,a.rsrv_num3,a.rsrv_num4,a.rsrv_num5,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,:OPER_CODE,a.rsrv_str11,a.rsrv_str12,a.rsrv_str13,a.rsrv_date1,a.rsrv_date2,a.rsrv_date3,:REMARK1,a.update_staff_id,a.update_depart_id,a.update_time,a.serial_number 

  from tf_f_user_mbmp_sub a
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND biz_type_code=:BIZ_TYPE_CODE
   AND biz_state_code=:BIZ_STATE_CODE
   AND (remark = :REMARK OR :REMARK='*')
   AND sysdate BETWEEN start_date AND end_date