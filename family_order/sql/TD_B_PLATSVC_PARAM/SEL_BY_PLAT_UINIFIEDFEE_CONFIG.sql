--IS_CACHE=Y
select distinct b.param_code  element_type_code,
                b.para_code1  discnt_code,
                b.para_code2  service_id,
				b.para_code3   enable_tag
  from td_s_commpara b
 where b.param_attr = '3746'
   and (b.eparchy_code = 'ZZZZ' or b.eparchy_code = :EPARCHY_CODE)
   and (b.param_code = 'S' or b.param_code = 'Z')
   and sysdate between b.start_date and b.end_date
