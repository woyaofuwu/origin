SELECT element_id, recomm_content,'1' choiceTag FROM TD_B_RECOMMPARA t
        WHERE  t.RECOMM_TYPE='2'
                and t.ELEMENT_ID(+)=:SERVICE_ID
        
        AND (t.eparchy_code=:TRADE_EPARCHY_CODE or t.eparchy_code='ZZZZ')
        AND  SYSDATE BETWEEN t.start_date AND t.end_date
        AND NOT EXISTS  (SELECT * FROM TD_B_RECOMMPARA m
                                                        	WHERE m.ELEMENT_ID=t.ELEMENT_ID
                                                        	AND m.RECOMM_TYPE=t.RECOMM_TYPE
                                                        	AND m.eparchy_code=t.eparchy_code
                                                        	AND m.UPDATE_TIME>t.UPDATE_TIME
                                                        	AND  SYSDATE BETWEEN m.start_date AND m.end_date )