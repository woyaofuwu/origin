select distinct a.cycle_id
  from td_b_cycle a,
       (select to_char(add_months(sysdate, -3 * 12), 'yyyymm') cycle_id
          from dual b) b,
       (select to_char(sysdate, 'yyyymm') cycle_id from dual c) c
 where to_date(a.cycle_id, 'yyyymm') between to_date(b.cycle_id, 'yyyymm') and
       to_date(c.cycle_id, 'yyyymm')
 order by cycle_id desc