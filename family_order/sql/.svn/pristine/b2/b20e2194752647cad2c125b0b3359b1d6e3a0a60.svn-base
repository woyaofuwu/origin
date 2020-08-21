--IS_CACHE=Y
SELECT ID sp_name_en,NAME biz_type,spid sp_id, bizcode sp_svc_id,biz_type_code,spname sp_name
FROM
(
SELECT  DISTINCT  a.sp_id ID,'['||a.sp_id||']'||sp_name NAME,'' SPID,'' BIZCODE,BIZ_TYPE_CODE,'' SPNAME
FROM td_m_spfactory a,td_m_spservice b
WHERE a.sp_id=b.sp_id
AND (biz_type_code IN ('01','02','03','04','05','06','07','08','09') OR biz_type_code IS NULL) AND b.biz_status NOT IN ('N','E') 
AND a.sp_status NOT IN ('N') AND rownum<10
UNION
SELECT a.sp_id||'&'||b.sp_svc_id,'['||b.sp_svc_id||']'||biz_type, a.sp_id,b.sp_svc_id,biz_type_code,'['||a.sp_id||']'||sp_name
FROM td_m_spservice b,td_m_spfactory a
WHERE a.sp_id=b.sp_id
AND (biz_type_code IN ('01','02','03','04','05','06','07','08','09') OR biz_type_code IS NULL) AND b.biz_status NOT IN ('N','E') 
AND a.sp_status NOT IN ('N') AND rownum<10
)