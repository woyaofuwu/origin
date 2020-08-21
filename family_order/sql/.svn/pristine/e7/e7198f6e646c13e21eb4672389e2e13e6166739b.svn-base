SELECT to_char(user_id) user_id,serial_number,type,long_content,deal_desc,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,to_char(in_date,'yyyy-mm-dd hh24:mi:ss') in_date,in_staff_id,in_depart_id,city_code,in_eparchy_code,remark 
  FROM tf_f_user_spec_complain
 WHERE in_date between TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') and TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')
   AND city_code=:CITY_CODE