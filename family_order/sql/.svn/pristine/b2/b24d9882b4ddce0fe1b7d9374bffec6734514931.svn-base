--IS_CACHE=Y
select t.province_code,
       t.biz_code,
       t.biz_name,
       t.sp_code,
       t.sp_name,
       t.sp_type,
       t.sp_attr,
       t.sp_desc,
       t.sp_status,
       t.province_no,
       t.record_status,
       to_char(t.record_date,'yyyy-mm-dd HH24:mi:ss') record_date,
       to_char(t.update_time,'yyyy-mm-dd HH24:mi:ss') update_time,
       t.update_staff_id,
       t.update_depart_id,
       t.remark,
       t.rsrv_str1,
       t.rsrv_str2,
       t.rsrv_str3,
       t.rsrv_str4,
       t.rsrv_str5,
       t.rsrv_str6,
       t.rsrv_str7,
       t.rsrv_str8,
       t.rsrv_str9,
       t.rsrv_str10
  from TD_M_SPINFO_CS t
 where t.PROVINCE_CODE = :PROVINCE_CODE
   and t.biz_code = :BIZ_CODE
   and t.sp_code = :SP_CODE