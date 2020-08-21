--IS_CACHE=Y
SELECT bank_code, bank, eparchy_code, super_bank_code
from td_b_bank
WHERE 1=1
AND bank_code like '%' || :BANK_CODE || '%'
AND bank like '%' || :BANK || '%'
AND eparchy_code = :EPARCHY_CODE
and super_bank_code = :SUPER_BANK_CODE