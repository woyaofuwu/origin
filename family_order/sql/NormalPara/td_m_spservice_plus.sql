--IS_CACHE=Y
select a.SP_ID||'|'||a.SP_SVC_ID||'|'||a.INFO_VALUE||'|' paraname,a.INFO_CODE paracode
 from td_m_spservice_plus a,td_m_spservice b
where a.sp_id = b.sp_id 
and a.sp_svc_id = b.sp_svc_id
and b.biz_type_code = 'A1'
AND (:TRADE_EPARCHY_CODE is null or :TRADE_EPARCHY_CODE is not null)