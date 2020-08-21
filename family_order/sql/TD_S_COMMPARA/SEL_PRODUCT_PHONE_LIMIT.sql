select 1 as RSRV_STR1
  from dual
 Where 1 in
       (Select 1
          From td_s_commpara a
         Where a.param_attr = '7640'
           and a.param_code = 'product_bind_serial_number'
           and a.subsys_code = 'CSM'
           and 1 = instr(:PHONECODE_S, a.para_code1, 1, 1)
           and sysdate between a.start_date and a.end_date)
union all
select 2 as RSRV_STR1
  from dual
 Where 2 in
       (Select 2
          From td_s_commpara b
         Where b.param_attr = '7640'
           and b.param_code = 'product_bind_serial_number'
           and b.subsys_code = 'CSM'
           and b.para_code2 = :PRODUCT_ID
           and sysdate between b.start_date and b.end_date)