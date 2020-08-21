SELECT partition_id,to_char(user_id) user_id,serial_number,biz_type_code,org_domain,opr_source,passwd,biz_state_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(rsrv_num1) rsrv_num1,to_char(rsrv_num2) rsrv_num2,to_char(rsrv_num3) rsrv_num3,to_char(rsrv_num4) rsrv_num4,to_char(rsrv_num5) rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time
FROM tf_f_user_mbmp
WHERE biz_state_code <> 'E'
AND sysdate between start_date and end_date+0
AND user_id = to_number(:USER_ID)
AND EXISTS  (SELECT 1
FROM td_s_tag
WHERE tag_code='PUB_CUR_PROVINCE'
AND use_tag='0'
AND start_date+0<sysdate
AND subsys_code='PUB'
AND end_date+0>=sysdate
AND INSTR(DECODE(tag_info,'HAIN','AN',biz_state_code),biz_state_code)>0
)