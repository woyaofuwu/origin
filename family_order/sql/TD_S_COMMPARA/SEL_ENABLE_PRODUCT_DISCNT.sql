--IS_CACHE=Y
select a.para_code1 discnt_code,a.param_name discnt_name 
       from td_s_commpara a
       where a.param_attr='811'
       and a.para_code2='1'
       and a.para_code3=:PRODUCT_ID
       and a.eparchy_code=:EPARCHY_CODE
       and a.end_date>sysdate