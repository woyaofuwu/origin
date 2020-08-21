SELECT count(1) recordcount
FROM tf_f_cust_groupmember_other a
 where a.remove_tag = '0'
	AND (a.cust_manager_id=:PARAM1 OR :PARAM1 IS NULL)
	AND (a.vip_tag=:PARAM2 OR :PARAM2 IS NULL)
	AND (a.serial_number=:PARAM3 OR :PARAM3 IS NULL)
        AND (a.member_kind = :PARAM15 OR :PARAM15 IS NULL)
	AND (a.cust_name like'%'||:PARAM4||'%' OR :PARAM4 IS NULL)
	AND (a.group_id like '%'||:PARAM5||'%' OR :PARAM5 IS NULL)
	AND (decode(a.vpmn_id,null,'0','','0','1')=:PARAM6 OR :PARAM6 IS NULL)
        AND (a.vpmn_id =:PARAM7 OR :PARAM7 IS NULL)
	AND (a.score_value>=:PARAM8 or :PARAM8 is null )
	AND (a.rsrv_2=:PARAM9 OR :PARAM9 IS NULL)
	AND (a.class_id=:PARAM10 OR :PARAM10 IS NULL)
        AND (a.rsrv_2=:PARAM16 OR :PARAM16 IS NULL)
        AND (a.prevaluec1=:PARAM17 OR :PARAM17 IS NULL)
        AND (a.credit_value>=:PARAM18 OR :PARAM18 IS NULL)
	AND (a.prevaluen3>=:PARAM11 or :PARAM11 is null)
	AND (a.open_date>=TO_DATE(:PARAM12 , 'YYYY-MM-DD HH24:MI:SS') OR :PARAM12 is null)
	AND (a.open_date<=TO_DATE(:PARAM13 , 'YYYY-MM-DD HH24:MI:SS') or :PARAM13 is null)
        AND exists
           ( select 1
             from td_m_staff b,td_m_depart c
	     WHERE a.cust_manager_id=b.staff_id
	     AND b.depart_id=c.depart_id
	     AND c.depart_frame LIKE
		(select t.depart_frame from td_m_depart t where t.depart_id=:PARAM14)||'%'
            )
        AND (
           exists (
                SELECT 1
                FROM tf_f_groupmember_product p
                WHERE p.serial_number=a.serial_number
                AND p.product_id=:PARAM0
                AND p.serial_number LIKE '%'||:PARAM3||'%'
           )
           OR :PARAM0 IS NULL
        )