--IS_CACHE=Y
select fee_code_rule_code FROM td_b_feecodetype
where eparchy_code=:EPARCHY_CODE and fee_code_rule_code=to_number(:FEE_CODE_RULE)