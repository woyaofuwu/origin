SELECT count(1) recordcount
  FROM tf_fh_cust_groupmember a
  where a.remove_tag = '0'
      AND (a.member_kind = :PARAM19 OR :PARAM19 IS NULL)
      AND a.backmonth=:PARAM17
      AND (a.cust_manager_id=:PARAM1 OR :PARAM1 IS NULL)
      AND (a.vip_tag=:PARAM5 OR :PARAM5 IS NULL)
      AND (a.serial_number=:PARAM2 OR :PARAM2 IS NULL)
      AND (a.cust_name like '%'||:PARAM4||'%' OR :PARAM4 IS NULL)
      AND (a.group_id  like '%'||:PARAM3||'%' OR :PARAM3 IS NULL)
      AND (a.prevalue1=:PARAM8 OR :PARAM8 IS NULL)
      AND (a.vpmn_group_id=:PARAM9 OR :PARAM9 IS NULL)
      AND (a.prevalue2=:PARAM10 OR :PARAM10 IS NULL)
      AND (a.contract_no=:PARAM11 OR :PARAM11 IS NULL)
      AND (a.score_value>=:PARAM12 or :PARAM12 is null)
      AND (a.basic_credit_value>=:PARAM13 or :PARAM13 is null)
      AND (a.rsrv_2=:PARAM14 OR :PARAM14 IS NULL)
      AND (a.class_id=:PARAM15 OR :PARAM15 IS NULL)
      AND (a.prevaluen3>=:PARAM16 or :PARAM16 is null )
      AND (a.open_date>=TO_DATE(:PARAM6 , 'YYYY-MM-DD HH24:MI:SS') OR :PARAM6 is null)
      AND (a.open_date<=TO_DATE(:PARAM7 , 'YYYY-MM-DD HH24:MI:SS') or :PARAM7 is null)
      and ((exists
        ( select 1
          from td_m_staff b,td_m_depart c
          WHERE a.cust_manager_id=b.staff_id
          AND b.depart_id=c.depart_id
          AND c.depart_frame LIKE
           (select t.depart_frame from td_m_depart t where t.depart_id=:PARAM0)||'%'
         ) ) or :PARAM0 = '49999' )
      AND (
           exists (
                SELECT 1
                FROM tf_f_groupmember_product p
                WHERE p.serial_number=a.serial_number
                AND p.product_id=:PARAM18
                AND p.serial_number LIKE '%'||:PARAM2||'%'
           )
           OR :PARAM18 IS NULL
        )