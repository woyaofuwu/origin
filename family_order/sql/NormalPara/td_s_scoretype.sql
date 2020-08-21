--IS_CACHE=Y
SELECT score_type_code paracode,score_type_name paraname FROM td_s_scoretype
where :TRADE_EPARCHY_CODE=:TRADE_EPARCHY_CODE