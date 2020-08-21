--IS_CACHE=Y
SELECT class_id paracode,class_name paraname FROM td_m_vipclass where :TRADE_EPARCHY_CODE IS NULL OR :TRADE_EPARCHY_CODE IS NOT NULL