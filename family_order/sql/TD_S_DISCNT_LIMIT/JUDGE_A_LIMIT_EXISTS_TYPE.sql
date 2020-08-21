select :DISCNT_CODE_A discnt_code_a,b.discnt_code discnt_code_b,a.limit_tag,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,a.eparchy_code
  from TD_S_DTYPE_LIMIT a,td_b_discnt b
 where a.discnt_type_a=(select discnt_type_code from td_b_discnt where discnt_code=:DISCNT_CODE_A)
   and b.discnt_type_code=a.discnt_type_b
   AND a.limit_tag=:LIMIT_TAG
   AND sysdate BETWEEN a.start_date AND a.end_date
   AND (a.eparchy_code=:EPARCHY_CODE OR a.eparchy_code='ZZZZ')
   and not exists(select 1
                    from td_s_discnt_limit c
                   where c.discnt_code_a=:DISCNT_CODE_A
                     and c.discnt_code_b=b.discnt_code
                     and c.limit_tag='4'
                     and sysdate BETWEEN c.start_date AND c.end_date
                     and (c.eparchy_code=:EPARCHY_CODE OR c.eparchy_code='ZZZZ'))
union all
SELECT discnt_code_a,discnt_code_b,limit_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code 
  FROM td_s_discnt_limit
 WHERE discnt_code_a=:DISCNT_CODE_A
   AND limit_tag=:LIMIT_TAG
   AND sysdate BETWEEN start_date AND end_date
   AND (eparchy_code=:EPARCHY_CODE OR eparchy_code='ZZZZ')