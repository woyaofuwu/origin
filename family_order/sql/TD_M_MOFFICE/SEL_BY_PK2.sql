--IS_CACHE=Y
SELECT switch_id
FROM td_m_moffice
WHERE moffice_id=:MOFFICE_ID
AND eparchy_code=:EPARCHY_CODE