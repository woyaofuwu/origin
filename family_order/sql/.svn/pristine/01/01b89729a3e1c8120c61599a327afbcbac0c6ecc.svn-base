--IS_CACHE=Y
select distinct a.para_value1|| '/' || a.para_code1 para_type,a.para_code1 
from td_m_res_para a 
where 1 = 1 
and (:PARA_ATTR is null or a.para_attr = :PARA_ATTR)
and (:VALID_TAG is null or a.valid_tag = :VALID_TAG)
order by a.para_code1