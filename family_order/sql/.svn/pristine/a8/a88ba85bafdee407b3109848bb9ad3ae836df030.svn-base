--IS_CACHE=Y
SELECT b.discnt_code para_code1,b.discnt_name para_code2,
'' para_code3, '' para_code4, '' para_code5, '' para_code6,
'' para_code7, '' para_code8, '' para_code9, '' para_code10,
'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,
'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM (
        SELECT NVL(s.data_code,'') data_code
        	FROM tf_m_staffdataright s,td_m_dataright d
        	WHERE s.right_attr='0'
        	  AND s.right_tag='1'
        	  AND s.data_code=d.data_code
        	  AND s.data_type='3'/*:DATA_TYPE*/
        	  AND s.data_type=d.data_type
        	  --AND s.data_code=to_char(:DATA_CODE)
            AND s.data_code IN (SELECT para_code1 FROM td_s_commpara 
                                WHERE subsys_code='CSM'
                                AND param_attr=512
                                AND param_code='0'
                                AND END_DATE > SYSDATE 
                                AND eparchy_code='0898')
        	  AND s.staff_id=:PARA_CODE1
            AND (s.rsvalue1 IS NULL OR s.rsvalue1 != '1')
        UNION 
         SELECT NVL(r.data_code,'') data_code
        	FROM tf_m_staffdataright s,tf_m_roledataright r
        	WHERE s.right_attr='1'
        	  AND s.right_tag='1'
        	  AND s.data_code=r.role_code
        	  AND s.data_type=r.data_type
        	  AND r.data_type='3'/*:DATA_TYPE*/
        	  --AND r.data_code=to_char(:DATA_CODE)
            AND r.data_code IN (SELECT para_code1 FROM td_s_commpara 
                                WHERE subsys_code='CSM'
                                AND param_attr=512
                                AND param_code='0'
                                AND END_DATE > SYSDATE 
                                AND eparchy_code='0898')
        	  AND NOT EXISTS(SELECT 1 FROM tf_m_staffdataright
        			          WHERE staff_id=s.staff_id
        			            AND data_code=r.data_code
        			            AND data_type=r.data_type)
        	  AND s.staff_id=:PARA_CODE1
            AND (s.rsvalue1 IS NULL OR s.rsvalue1 != '1')
        UNION 
           SELECT para_code1 data_code FROM td_s_commpara 
            WHERE subsys_code='CSM'
            AND param_attr=512
            AND param_code='0'
            AND END_DATE > SYSDATE 
            AND eparchy_code='0898'
            AND :PARA_CODE1 = 'SUPERUSR'
        ) a ,td_b_discnt b
        WHERE to_number(a.data_code) = b.discnt_code
        
AND (:PARA_CODE2 IS NULL OR :PARA_CODE2='')
AND (:PARA_CODE3 IS NULL OR :PARA_CODE3='')
AND (:PARA_CODE4 IS NULL OR :PARA_CODE4='')
AND (:PARA_CODE5 IS NULL OR :PARA_CODE5='')
AND (:PARA_CODE6 IS NULL OR :PARA_CODE6='')
AND (:PARA_CODE7 IS NULL OR :PARA_CODE7='')
AND (:PARA_CODE8 IS NULL OR :PARA_CODE8='')
AND (:PARA_CODE9 IS NULL OR :PARA_CODE9='')
AND (:PARA_CODE10 IS NULL OR :PARA_CODE10='')