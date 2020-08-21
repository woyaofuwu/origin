--IS_CACHE=Y
select t.DATA_NAME,t.pindex,t.sel_mode,t.tab_name,t.sql_ref,t.query_params,t.option_params,t.option_value,ROUTE_ID,t.rsrv_str1,t.rsrv_str2,t.rsrv_str3
from TD_S_TRADETYPE_PF_DATA t 
where t.DATA_NAME = :DATA_NAME
order by t.DATA_NAME,t.pindex