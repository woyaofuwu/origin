--IS_CACHE=Y
select a.rule_id,a.gift_type_code,a.rule_name
from td_b_exchange_rule a,td_b_score_exchange_type b
where a.status = '0'
and  a.exchange_type_code = b.exchange_type_code(+)
and a.exchange_type_code = 'C'
and b.eparchy_code = :EPARCHY_CODE
and a.score <= :SCORE
and (a.eparchy_code = :EPARCHY_CODE OR A.EPARCHY_CODE = 'ZZZZ')
and (a.brand_code = :BRAND_CODE OR a.brand_code = 'G000')