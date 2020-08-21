--IS_CACHE=Y
select rowid,
       subsys_code,
       param_attr,
       param_code,
       param_name,
       para_code1,
       para_code2,
       para_code3,
       para_code4,
       para_code5,
       para_code6,
       para_code7,
       para_code8,
       para_code9,
       para_code10,
       para_code11,
       para_code12,
       para_code13,
       start_date,
       end_date,
       eparchy_code,
       update_staff_id,
       update_depart_id,
       update_time,
       remark
  from TD_S_COMMPARA
 where PARAM_ATTR = :PARAM_ATTR
   and (EPARCHY_CODE = :EPARCHY_CODE or EPARCHY_CODE = 'ZZZZ')
   and end_date>sysdate
 order by param_code