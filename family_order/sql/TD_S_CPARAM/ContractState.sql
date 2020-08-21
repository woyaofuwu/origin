SELECT 'ContractState' KEY, contract_state_code VALUE1,'-1' VALUE2,
contract_state VRESULT
     FROM td_s_contract_state
WHERE 'ContractState'=:KEY