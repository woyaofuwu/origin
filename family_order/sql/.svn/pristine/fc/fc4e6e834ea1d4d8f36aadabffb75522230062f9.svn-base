SELECT partition_id,
       to_char(user_id) user_id,
       to_char(user_id_a) user_id_a,
       to_char(discnt_code) DISCNT_CODE,
       spec_tag,
       relation_type_code,
       to_char(inst_id) INST_ID,
       to_char(campn_id) campn_id,
       to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date,
       to_char(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,
       update_staff_id,
       update_depart_id,
       remark,
       to_char(rsrv_num1) rsrv_num1,
       to_char(rsrv_num2) rsrv_num2,
       to_char(rsrv_num3) rsrv_num3,
       to_char(rsrv_num4) rsrv_num4,
       to_char(rsrv_num5) rsrv_num5,
       rsrv_str1,
       rsrv_str2,
       rsrv_str3,
       rsrv_str4,
       rsrv_str5,
       to_char(rsrv_date1, 'yyyy-mm-dd hh24:mi:ss') rsrv_date1,
       to_char(rsrv_date2, 'yyyy-mm-dd hh24:mi:ss') rsrv_date2,
       to_char(rsrv_date3, 'yyyy-mm-dd hh24:mi:ss') rsrv_date3,
       rsrv_tag1,
       rsrv_tag2,
       rsrv_tag3
  FROM tf_f_user_discnt 
  WHERE user_id = TO_NUMBER(:USER_ID)
      AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
      /*AND end_date > TO_DATE('2050-01-01 00:00:00','yyyy-mm-dd hh24:mi:ss')*/
	  AND end_date > TO_DATE(TO_CHAR(TRUNC(LAST_DAY(SYSDATE)+1)-1/(24*3600),'YYYY-MM-DD hh24:mi:ss'),'yyyy-mm-dd hh24:mi:ss')
      AND START_DATE < END_DATE
