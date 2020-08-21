--IS_CACHE=Y
select a.feeitem_code,a.feeitem_name,a.pre_tag,a.premoney 
from td_b_feeitem a 
where eparchy_code = :EPARCHY_CODE
and sysdate between a.start_date and a.end_date