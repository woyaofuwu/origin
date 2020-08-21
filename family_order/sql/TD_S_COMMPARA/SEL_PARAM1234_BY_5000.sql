--IS_CACHE=Y
select b.param_code,
       b.para_code1 / 100 m_money,
       b.para_code2 m_count,
       b.para_code3 / 100 t_money,
       b.para_code4 t_count
  from td_s_commpara b
 where subsys_code = 'CSM'
   and param_attr = '5000'