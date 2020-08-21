SELECT a.partition_id,
       to_char(a.user_id) user_id,
       to_char(a.user_id_a) user_id_a,
       to_char(b.offer_code) product_id,
       to_char(b.group_id) group_id,
       to_char(a.discnt_code) discnt_code,
       a.spec_tag,
       a.relation_type_code,
       to_char(a.inst_id) inst_id,
       to_char(a.campn_id) campn_id,
       to_char(a.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(a.end_date, 'yyyy-mm-dd hh24:mi:ss') end_date,
       to_char(a.update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,
       a.update_staff_id,
       a.update_depart_id,
       a.remark,
       to_char(a.rsrv_num1) rsrv_num1,
       to_char(a.rsrv_num2) rsrv_num2,
       to_char(a.rsrv_num3) rsrv_num3,
       to_char(a.rsrv_num4) rsrv_num4,
       to_char(a.rsrv_num5) rsrv_num5,
       a.rsrv_str1,
       a.rsrv_str2,
       a.rsrv_str3,
       a.rsrv_str4,
       a.rsrv_str5,
       to_char(a.rsrv_date1, 'yyyy-mm-dd hh24:mi:ss') rsrv_date1,
       to_char(a.rsrv_date2, 'yyyy-mm-dd hh24:mi:ss') rsrv_date2,
       to_char(a.rsrv_date3, 'yyyy-mm-dd hh24:mi:ss') rsrv_date3,
       a.rsrv_tag1,
       a.rsrv_tag2,
       a.rsrv_tag3
  FROM tf_f_user_discnt a ,tf_f_user_offer_rel b
 WHERE a.user_id = TO_NUMBER(:USER_ID)
   AND a.partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   AND a.inst_id = b.rel_offer_ins_id
   AND b.user_id = TO_NUMBER(:USER_ID)
   AND b.partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   AND (b.group_id = TO_NUMBER(:GROUP_ID) or :GROUP_ID is null)
 ORDER BY a.start_date, a.end_date