--IS_CACHE=Y
select nvl(a.PARAM_CODE,'') as PARAM_CODE,nvl(a.PARAM_NAME,'') as PARAM_NAME,nvl(a.PARA_CODE1,'') as PARA_CODE1,
nvl(a.PARA_CODE2,'') as PARA_CODE2,nvl(a.PARA_CODE3,'') as PARA_CODE3,nvl(a.PARA_CODE4,'') as PARA_CODE4,
nvl(a.PARA_CODE5,'') as PARA_CODE5,nvl(a.PARA_CODE6,'') as PARA_CODE6,nvl(a.PARA_CODE7,'') as PARA_CODE7,
nvl(a.PARA_CODE8,'') as PARA_CODE8,nvl(a.PARA_CODE9,'') as PARA_CODE9,nvl(a.PARA_CODE10,'') as PARA_CODE10,
nvl(a.PARA_CODE11,'') as PARA_CODE11,nvl(a.PARA_CODE12,'') as PARA_CODE12,nvl(a.PARA_CODE13,'') as PARA_CODE13,
nvl(a.PARA_CODE14,'') as PARA_CODE14,nvl(a.PARA_CODE15,'') as PARA_CODE15,nvl(a.PARA_CODE16,'') as PARA_CODE16,
nvl(a.PARA_CODE17,'') as PARA_CODE17,nvl(a.PARA_CODE18,'') as PARA_CODE18,nvl(a.PARA_CODE19,'') as PARA_CODE19,
nvl(a.PARA_CODE20,'') as PARA_CODE20,nvl(a.PARA_CODE21,'') as PARA_CODE21,nvl(a.PARA_CODE22,'') as PARA_CODE22,
nvl(a.PARA_CODE23,'') as PARA_CODE23,nvl(a.PARA_CODE24,'') as PARA_CODE24,nvl(a.PARA_CODE25,'') as PARA_CODE25,
nvl(a.PARA_CODE26,'') as PARA_CODE26,nvl(a.PARA_CODE27,'') as PARA_CODE27,nvl(a.PARA_CODE28,'') as PARA_CODE28,
nvl(a.PARA_CODE29,'') as PARA_CODE29,nvl(a.PARA_CODE30,'') as PARA_CODE30 
from td_s_commpara a 
where a.subsys_code=:SUBSYS_CODE 
and a.param_attr=:PARAM_ATTR 
and a.PARA_CODE12='1' 
and end_date>sysdate
order by PARA_CODE9 asc