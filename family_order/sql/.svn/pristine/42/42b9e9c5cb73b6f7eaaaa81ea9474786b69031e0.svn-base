--IS_CACHE=Y
SELECT code_type_code 
  FROM td_m_departkind
 WHERE eparchy_code = :EPARCHY_CODE 
   AND depart_kind_code = (SELECT depart_kind_code 
                                 FROM td_m_depart 
                                WHERE depart_id = :DEPART_ID)