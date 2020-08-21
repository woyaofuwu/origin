--IS_CACHE=Y
SELECT vip_type_code paracode,vip_type paraname FROM td_m_viptype where (:TRADE_EPARCHY_CODE IS NOT NULL OR :TRADE_EPARCHY_CODE IS NULL) and vip_type_code = '1'