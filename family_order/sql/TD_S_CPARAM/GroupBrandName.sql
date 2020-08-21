--IS_CACHE=Y
SELECT 'GroupBrandName' KEY,param_code VALUE1,para_code1 VALUE2,para_code3 VRESULT 
FROM td_s_commpara 
 where PARAM_ATTR='998' 
  and sysdate between start_date and end_date
     and eparchy_code='ZZZZ'
       and 'GroupBrandName'=:KEY