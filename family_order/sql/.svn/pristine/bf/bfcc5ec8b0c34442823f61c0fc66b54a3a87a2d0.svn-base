SELECT distinct staff_id,data_code,data_type,right_attr,right_class,oper_special,right_tag,rsvalue1,rsvalue2,remark,accredit_time,accredit_staff_id 
  FROM (
        SELECT s.staff_id staff_id,NVL(s.data_code,'') data_code,NVL(s.data_type,'') data_type,
                NVL(s.right_attr,'') right_attr,
              	NVL(s.right_tag,'') right_tag,NVL(s.right_class,'') right_class,NVL(s.oper_special,'') oper_special,
              	NVL(s.rsvalue1,'') rsvalue1, NVL(s.rsvalue2,'') rsvalue2,
              	NVL(TO_CHAR(s.accredit_time,'yyyy/mm/dd hh24:mi:ss'),'') accredit_time,
              	NVL(rtrim(s.accredit_staff_id),'') accredit_staff_id,NVL(s.remark,'') remark
        	FROM tf_m_staffdataright s
        	WHERE s.right_attr='0'
        	  AND s.right_tag='1'
        	  AND s.data_type=:DATA_TYPE
        	  AND s.data_code=to_char(:DATA_CODE)
        	  AND s.staff_id=:STAFF_ID
            AND (s.rsvalue1 IS NULL OR s.rsvalue1 != '1')
        UNION ALL
         SELECT s.staff_id staff_id,NVL(r.data_code,'') data_code,NVL(s.data_type,'') data_type,
                NVL(s.right_attr,'') right_attr,
              	NVL(s.right_tag,'') right_tag,NVL(r.right_class,'') right_class,NVL(r.oper_special,'') oper_special,
              	NVL(r.rsvalue1,'') rsvalue1,NVL(r.rsvalue2,'') rsvalue2,
              	NVL(TO_CHAR(s.accredit_time,'yyyy/mm/dd hh24:mi:ss'),' ') accredit_time,
              	NVL(RTRIM(s.accredit_staff_id),' ') accredit_staff_id,
              	NVL(s.remark,' ') remark
        	FROM tf_m_staffdataright s,tf_m_roledataright r
        	WHERE s.right_attr='1'
        	  AND s.right_tag='1'
        	  AND s.data_code=r.role_code
        	  AND s.data_type=r.data_type
        	  AND r.data_type=:DATA_TYPE
        	  AND r.data_code=to_char(:DATA_CODE)
        	  /*AND NOT EXISTS(SELECT 1 FROM tf_m_staffdataright
        			          WHERE staff_id=s.staff_id
        			            AND data_code=r.data_code
        			            AND data_type=r.data_type)*/
        	  AND s.staff_id=:STAFF_ID
            AND (s.rsvalue1 IS NULL OR s.rsvalue1 != '1')
        UNION ALL
           SELECT to_char(:STAFF_ID) staff_id,to_char(:DATA_CODE) data_code,to_char(:DATA_TYPE) data_type,
                  '0' right_attr,'1' right_tag,'7' right_class,
                  '2' oper_special,'2' rsvalue1,'' rsvalue2,
                  '' accredit_time,'' accredit_staff_id,'' remark
             FROM dual
            WHERE :STAFF_ID = 'SUPERUSR'
        )
        ORDER BY data_code,right_class desc