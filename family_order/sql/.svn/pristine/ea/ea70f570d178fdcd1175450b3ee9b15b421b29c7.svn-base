--IS_CACHE=Y
SELECT staff_id,right_code,right_attr,extend_value1,extend_value2,right_tag,rsvalue1,rsvalue2,remark,to_char(accredit_time,'yyyy-mm-dd hh24:mi:ss') accredit_time,accredit_staff_id 
  FROM (
            SELECT  s.staff_id staff_id,NVL(s.right_code,'') right_code ,
                	NVL(f.right_attr,'') right_attr,NVL(s.right_tag,'') right_tag,
                	NVL(s.extend_value1,'') extend_value1,NVL(s.extend_value2,0) extend_value2,
                	NVL(s.rsvalue1,'') rsvalue1,NVL(s.rsvalue2,'') rsvalue2,
                	NVL(TO_CHAR(s.accredit_time,'yyyy/mm/dd hh24:mi:ss'),' ') accredit_time,
                	NVL(RTRIM(s.accredit_staff_id),'') accredit_staff_id,NVL(s.remark,'') remark
            	FROM tf_m_stafffuncright s,td_m_funcright f,td_s_modfile m
            	WHERE s.right_attr='0'
            	  AND s.right_tag='1'
            	  AND f.mod_code=m.mod_code(+)
            	  AND s.right_code=f.right_code
            	  AND (f.right_attr='1' OR f.right_attr='2')
            	  AND s.right_code=:RIGHT_CODE
            	  AND s.staff_id=:STAFF_ID
                  AND (s.rsvalue1 IS NULL OR s.rsvalue1 != '1')
            UNION
            	SELECT s.staff_id,NVL(r.right_code,'') right_code ,
                	NVL(f.right_attr,'') right_attr,NVL(s.right_tag,'') right_tag,
                	NVL(r.extend_value1,'') extend_value1,NVL(r.extend_value2,0) extend_value2,
                	NVL(r.rsvalue1,'') rsvalue1, NVL(r.rsvalue2,'') rsvalue2,
                	NVL(TO_CHAR(s.accredit_time,'yyyy/mm/dd hh24:mi:ss'),'') accredit_time,
                	NVL(RTRIM(s.accredit_staff_id),'') accredit_staff_id,
                	NVL(s.remark,'') remark
            	FROM tf_m_stafffuncright s,tf_m_rolefuncright r,td_s_modfile m,td_m_funcright f
            	WHERE s.right_attr='1'
            	  AND s.right_tag='1'
            	  AND s.right_code=r.role_code
            	  AND r.right_code=f.right_code
            	  AND f.mod_code=m.mod_code(+)
            	  AND f.right_code=:RIGHT_CODE
            	  AND (f.right_attr='1' OR f.right_attr='2')
            	  AND NOT EXISTS( SELECT 1 FROM tf_m_stafffuncright
            			          WHERE staff_id=s.staff_id
            			            AND right_code=r.right_code)
            	  AND s.staff_id=:STAFF_ID
                  AND (s.rsvalue1 IS NULL OR s.rsvalue1 != '1')
)