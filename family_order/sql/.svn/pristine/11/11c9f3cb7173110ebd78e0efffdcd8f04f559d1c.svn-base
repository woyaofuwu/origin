--IS_CACHE=Y
select res_id,res_name,card_kind_code 
from td_m_resgift
 where res_type_code=:RES_TYPE_CODE
 and (eparchy_code=:EPARCHY_CODE or eparchy_code='ZZZZ')
 and res_kind_code=:RES_KIND_CODE