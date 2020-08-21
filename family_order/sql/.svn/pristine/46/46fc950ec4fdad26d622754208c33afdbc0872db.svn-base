update tf_F_user_discnt a
        set end_date = trunc(sysdate,'mm')-1/24/60/60,REMARK = '自己产品底下的优惠结束倒是上月底'
        where a.user_id = :USER_ID
        and a.partition_id = mod(to_number(:USER_ID), 10000)
        and exists (select 1 from td_S_commpara b
                             where b.subsys_code = 'CSM'
                             and b.param_attr = '8850'
                             and b.param_code='1'
                             and b.para_code1 = to_char(a.discnt_code)
                             and b.end_date>sysdate)
        and a.end_date + 0 > trunc(sysdate,'mm')
        and a.end_date + 0 <= trunc(add_months(sysdate,1),'mm')-1/24/60/60