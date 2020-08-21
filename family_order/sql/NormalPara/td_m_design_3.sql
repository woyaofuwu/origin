--IS_CACHE=Y
SELECT DESIGN_CODE paracode,DESIGN_NAME paraname FROM td_m_design
 WHERE release_eparchy_code=:TRADE_EPARCHY_CODE
   AND start_date<=SYSDATE
   AND end_date>=SYSDATE 
   AND code_state_code='1'
   AND res_type_code='3'