--IS_CACHE=Y
select s.script_id, s.ver, s.script_type, s.exec_script, d.rule_param, d.rule_id, d.is_revolt, d.rule_name 
  from TD_RULE_DEF d, TD_RULE_FLOW F, td_rule_script_release s                         
where  f.rule_id = d.rule_id                                          				         
  and  s.script_id = d.script_id												
  and  s.script_type in('GV', 'JB')											         
  and  sysdate between f.start_date and f.end_date		                                 
  and  d.state  = '0'																													         
  and  f.rule_biz_id = :RULE_BIZ_ID                                                   
  and  (d.right_code IS NULL                                                           
     OR NOT EXISTS (SELECT d.data_code FROM tf_m_staffdataright C,tf_m_roledataright D 
                          WHERE 1=1                                                    
                            AND C.staff_id = :STAFF_ID                                
                            AND C.data_code = D.role_code                              
                            AND C.right_attr = '1'   --权限属性：1-数据角色权限        
                            AND C.right_tag = '1'    --权限标志：1-有效                
                            AND D.data_type = '1'    --数据类型：1-数据特权            
                            AND D.data_code = d.right_code)                            
     AND NOT EXISTS (SELECT e.data_code FROM tf_m_staffdataright e                     
                          WHERE 1=1                                                    
                            AND  e.staff_id = :STAFF_ID                               
                                  AND e.right_attr = '0'                               
                                  AND e.data_type = '1'                                
                                  AND e.right_tag = '1'                                
                                  AND e.data_code = d.right_code))                     
order by f.exec_order