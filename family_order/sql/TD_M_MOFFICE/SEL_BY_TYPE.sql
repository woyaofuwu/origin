--IS_CACHE=Y
select distinct(switch_id) switch_id ,rsrv_str1,rsrv_str2 from td_m_moffice 
where eparchy_code=:EPARCHY_CODE