--IS_CACHE=Y
select a.param_value2, a.param_value3, a.param_value4, a.param_value5,DECODE(a.param_value6,'1',to_char(add_months(last_day(trunc(sysdate)), 0)+1 ,'yyyy-mm-dd'),'') param_value6
from td_b_commconfig a 
where a.config_domain = '01' and a.config_type = '13' 
      and a.param_code = :PARAM_CODE
      and a.param_value1 = :PARAM_VALUE1