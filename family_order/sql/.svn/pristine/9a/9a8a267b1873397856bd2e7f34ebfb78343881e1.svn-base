--IS_CACHE=Y
select param_attr from td_s_commpara                         
 where subsys_code = 'CSM'                                    
 and   param_attr in('4444','4445','4446','4447','4448')      
 and   param_code = :TRADE_TYPE_CODE                          
 and   (eparchy_code = :EPARCHY_CODE OR eparchy_code = 'ZZZZ')
 and   sysdate between start_date and end_date