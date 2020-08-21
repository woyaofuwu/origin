INSERT INTO tf_f_user_plat_register(partition_id,user_id,serial_number,biz_type_code,org_domain,opr_source,
passwd,biz_state_code,open_tag,svc_level,start_date,end_date,rsrv_num1,rsrv_num2,rsrv_num3,rsrv_num4,rsrv_num5,
rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_date1,rsrv_date2,rsrv_date3,remark,update_staff_id,
update_depart_id,update_time) 
SELECT /*+ index(a PK_TF_F_USER_PLAT_REGISTER)*/partition_id,user_id,serial_number,biz_type_code,org_domain,opr_source,passwd,biz_state_code,:OPEN_TAG,
svc_level,to_date(:DATE,'yyyy-mm-dd hh24:mi:ss'),to_date('2050-12-31 23:59:59','yyyy-mm-dd hh24:mi:ss'),rsrv_num1,rsrv_num2,rsrv_num3,
rsrv_num4,rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_date1,rsrv_date2,rsrv_date3,
a.remark,:UPDATE_STAFF_ID,:UPDATE_DEPART_ID,SYSDATE
  FROM tf_f_user_plat_register a, td_s_commpara b
 WHERE a.partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND a.user_id=TO_NUMBER(:USER_ID)
   AND a.biz_type_code = b.param_code
   AND b.param_attr = 934
   AND b.subsys_code = 'CSM'
   AND b.eparchy_code = 'ZZZZ'
   AND a.end_date = to_date(:DATE,'yyyy-mm-dd hh24:mi:ss')-1/24/3600
   AND SYSDATE BETWEEN b.start_date AND b.end_date