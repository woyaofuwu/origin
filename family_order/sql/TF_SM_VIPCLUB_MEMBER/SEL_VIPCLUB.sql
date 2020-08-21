SELECT member_id,club_id,vip_id,cust_name,vip_type_code,class_id,serial_number,vip_no,to_char(join_date,'yyyy-mm-dd hh24:mi:ss') join_date,to_char(birth_yearmonth,'yyyy-mm-dd hh24:mi:ss') birth_yearmonth,contract_phone,to_char(parfor_score) parfor_score,to_char(create_time,'yyyy-mm-dd hh24:mi:ss') create_time,create_staff_id,remart,to_char(prevaluen1) prevaluen1,prevaluev1,prevaluev2,prevaluev3,prevaluev4,to_char(prevalued1,'yyyy-mm-dd hh24:mi:ss') prevalued1,rsrv_str1,rsrv_str3
  FROM tf_sm_vipclub_member
 WHERE club_id=:CLUB_ID
   AND serial_number=:SERIAL_NUMBER