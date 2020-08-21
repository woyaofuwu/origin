--IS_CACHE=Y
select RULE_BIZ_ID, RULE_BIZ_CHK, TIPS_TYPE, TIPS_INFO                                      
  from td_rule_biz b                                                                        
where  1=1                                                                                  
  and  sysdate between b.start_date and b.end_date                                          
  and  b.rule_biz_type_code = :RULE_BIZ_TYPE_CODE		
  and  b.rule_biz_kind_code = :RULE_BIZ_KIND_CODE												              
  and  (b.trade_type_code = -1 or b.trade_type_code = :TRADE_TYPE_CODE)	                  
  and  (b.eparchy_code = 'ZZZZ' or b.eparchy_code = :EPARCHY_CODE)			                    
  and  (b.brand_code = 'ZZZZ' or b.brand_code = :BRAND_CODE)						                    
  and  (b.product_id = -1 or b.product_id = :PRODUCT_ID)		                                
  and  (b.package_id = -1 or b.package_id = :PACKAGE_ID)
  and  (b.right_code IS NULL                                                                
         or not exists (select d.data_code from tf_m_staffdataright c,tf_m_roledataright d  
                              where c.staff_id = :STAFF_ID                                 
                                and c.data_code = d.role_code                               
                                and c.right_attr = '1'   --权限属性：1-数据角色权限         
                                and c.right_tag = '1'    --权限标志：1-有效                 
                                and d.data_type = '1'    --数据类型：1-数据特权             
                                and d.data_code = b.right_code)                             
         and not exists (select e.data_code from tf_m_staffdataright e                      
                                    where e.staff_id = :STAFF_ID                           
                                      and e.right_attr = '0'                                
                                      and e.data_type = '1'                                 
                                      and e.right_tag = '1'                                 
                                      and e.data_code = b.right_code)                       
       )                                                                                    
order by b.exec_order