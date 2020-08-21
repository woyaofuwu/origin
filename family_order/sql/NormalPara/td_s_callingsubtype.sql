--IS_CACHE=Y
SELECT calling_sub_type_code paracode,calling_sub_type_name paraname FROM td_s_callingsubtype where :TRADE_EPARCHY_CODE IS NOT NULL OR :TRADE_EPARCHY_CODE IS NULL