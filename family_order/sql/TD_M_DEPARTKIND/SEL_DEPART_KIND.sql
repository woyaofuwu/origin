--IS_CACHE=Y
SELECT depart_kind_code,depart_kind
FROM td_m_departkind
 WHERE eparchy_code = :EPARCHY_CODE