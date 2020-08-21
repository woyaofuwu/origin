select t.partition_id,
       t.user_id,
       t.user_id_a,
       t.DISCNT_CODE,
       t.spec_tag,
       t.relation_type_code,
       t.inst_id,
       t.campn_id,
       to_char(t.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(t.end_date, 'yyyy-mm-dd hh24:mi:ss') end_date,
       to_char(t.update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,
       t.update_staff_id,
       t.update_depart_id,
       t.remark,
       t.rsrv_num1,
       t.rsrv_num2,
       t.rsrv_num3,
       t.rsrv_num4,
       t.rsrv_num5,
       t.rsrv_str1,
       t.rsrv_str2,
       t.rsrv_str3,
       t.rsrv_str4,
       t.rsrv_str5,
       t.rsrv_date1,
       t.rsrv_date2,
       t.rsrv_date3,
       t.rsrv_tag1,
       t.rsrv_tag2,
       t.rsrv_tag3
  from TF_F_USER_DISCNT t
  WHERE partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   AND user_id = TO_NUMBER(:USER_ID)
   AND discnt_code in (
   SELECT param_code 
  FROM td_s_commpara 
 WHERE subsys_code=:SUBSYS_CODE
   AND param_attr=:PARAM_ATTR
   AND para_code1=:PARA_CODE1
   ) 
   AND sysdate BETWEEN start_date AND end_date