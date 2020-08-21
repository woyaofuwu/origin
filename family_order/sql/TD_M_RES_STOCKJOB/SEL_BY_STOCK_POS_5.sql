--IS_CACHE=Y
select stock_pos, job, res_type_code 
from  
( 
select stock_pos, job, res_type_code 
from td_m_res_stockjob   
where (stock_pos = :STOCK_POS OR :STOCK_POS IS NULL) 
group by stock_pos, job, res_type_code 
) 
where (job = :JOB OR :JOB IS NULL) 
AND (res_type_code = :RES_TYPE_CODE OR :RES_TYPE_CODE IS NULL)