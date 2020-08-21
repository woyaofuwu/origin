select * from (
select a.user_id user_id,a.discnt_code discnt_code,a.inst_id inst_id,to_char(a.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       b.para_code1/100 m_money,b.para_code2 m_count,b.para_code3/100 t_money,b.para_code4 t_count
 from (select *
        from tf_b_trade_discnt t
        where t.modify_tag = '0'
        and t.trade_id in (select trade_id from tf_b_trade where net_type_code in ('11')
                            and trade_type_code in ('9711','9712','9725') and serial_number = :SERIAL_NUMBER)
        and t.end_date > sysdate) a 
  inner join (select * from td_s_commpara where subsys_code='CSM' and param_attr='5000' )b on a.discnt_code = b.param_code
 union all
  select a.user_id user_id,a.discnt_code discnt_code, a.inst_id inst_id,to_char(a.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
         b.para_code1/100 m_money,b.para_code2 m_count,b.para_code3/100 t_money,b.para_code4 t_count
  from (select t.*
         from tf_f_user_discnt t
         where t.user_id in (select user_id from tf_f_user  where net_type_code in ('11')
                              and remove_tag = '0' and serial_number = :SERIAL_NUMBER)
         and t.end_date > sysdate) a
 inner join (select * from td_s_commpara where subsys_code='CSM' and param_attr='5000' )b on a.discnt_code = b.param_code
) order by start_date desc