SELECT partition_id,to_char(user_id) user_id,to_char(inst_id) inst_id,serial_number,biz_type_code,biz_code,biz_name,access_number, biz_state_code,price,billing_type,biz_pri,biz_status,cs_url,usage_desc,intro_url,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(first_date,'yyyy-mm-dd hh24:mi:ss') first_date,rsrv_num1,rsrv_num2,rsrv_num3,to_char(rsrv_num4) rsrv_num4,to_char(rsrv_num5) rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM tf_f_user_grp_platsvc platsvc
 WHERE (platsvc.biz_state_code=:BIZ_STATE_CODE OR platsvc.biz_status=:BIZ_STATE_CODE)
   and platsvc.end_date> sysdate and exists (
       select 1 from tf_f_user u where u.user_id=platsvc.user_id 
          and u.remove_tag = '0'
          and u.cust_id = TO_NUMBER(:CUST_ID)
   )