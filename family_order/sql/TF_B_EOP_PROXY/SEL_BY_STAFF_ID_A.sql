select t.proxy_id,
       t.staff_id_a,
       t.staff_name_a,
       t.staff_phone_a,
       t.staff_id_b,
       t.staff_name_b,
       t.staff_phone_b,
       t.proxy_type,
       t.role_id,
       t.info_list,
       TO_CHAR(T.start_date, 'YYYY-MM-DD HH24:MI:SS') start_date,
       TO_CHAR(T.end_date, 'YYYY-MM-DD HH24:MI:SS') end_date,
       t.update_time,
       t.rsrv_str1,
       t.rsrv_str2,
       t.rsrv_str3,
       t.rsrv_str4,
       t.remark
  from TF_B_EOP_PROXY t
 WHERE staff_id_a = :STAFF_ID_A
   and t.end_date > sysdate
   order by t.proxy_id desc