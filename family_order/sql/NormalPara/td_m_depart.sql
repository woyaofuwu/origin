--IS_CACHE=Y
SELECT depart_id paracode,depart_name paraname FROM td_m_depart
where (:TRADE_EPARCHY_CODE IS NOT NULL OR :TRADE_EPARCHY_CODE IS NULL) and
VALIDFLAG ='0'