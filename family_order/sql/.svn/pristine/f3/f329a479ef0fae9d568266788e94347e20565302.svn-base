--IS_CACHE=Y
SELECT a.sp_id sp_id,
       a.sp_svc_id sp_svc_id,
       a.biz_type biz_type,
       a.biz_type_code biz_type_code,
       a.biz_desc biz_desc,
       trim(to_char(a.price/1000, '99990.0')) price,
       decode(a.billing_type,'0','免费','1','按条计费','2','包月计费','3','包时计费','4','包次计费','未知')  billing_type,
       b.sp_name sp_name,
       b.sp_name_en sp_name_en,
       b.sp_short_name sp_short_name,
       b.sp_status sp_status,
       b.sp_desc sp_desc,
       b.cs_tel cs_tel,
       a.rsrv_str9 rsrv_str9
  FROM td_m_spservice a, td_m_spfactory b
 WHERE a.sp_id = b.sp_id
   AND a.biz_type like '%'||:BIZ_TYPE||'%'
   AND b.sp_name like '%'||:SP_NAME||'%'