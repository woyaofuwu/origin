--IS_CACHE=Y
select count(*) recordcount From td_m_area WHERE area_level='30'
  AND area_code=:CITY_CODE
    AND parent_area_code=:EPARCHY_CODE