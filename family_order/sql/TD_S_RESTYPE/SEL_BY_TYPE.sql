--IS_CACHE=Y
select res_kind_code,kind_name
 from td_s_reskind 
 where res_type_code=:RES_TYPE_CODE
  and (eparchy_code=:EPARCHY_CODE or eparchy_code='ZZZZ')