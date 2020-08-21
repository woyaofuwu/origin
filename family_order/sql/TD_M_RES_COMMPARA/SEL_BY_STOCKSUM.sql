--IS_CACHE=Y
select SUM(para_value11) para_value11
 from td_m_res_commpara  
 where para_attr=1034   
 and eparchy_code=:EPARCHY_CODE