--IS_CACHE=Y
select a.area_code, a.area_name, b.pdata_id
  from td_m_area a, td_s_static b
 where a.parent_area_code = b.data_id
   and a.area_code = :EPARCHY_CODE
   and b.type_id = 'PROVINCE_CODE'