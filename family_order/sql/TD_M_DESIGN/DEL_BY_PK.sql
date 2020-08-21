DELETE FROM td_m_design
 WHERE release_eparchy_code=:RELEASE_EPARCHY_CODE
   AND design_code=:DESIGN_CODE
   AND start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')