--IS_CACHE=Y
select cp.* from td_b_compitem c, td_s_commpara cp 
 where c.item_id = :ITEM_ID 
   and cp.subsys_code = :SUBSYS_CODE
   and cp.param_attr = :PARAM_ATTR
   and cp.param_code = :PARAM_CODE
   and cp.param_name = c.sub_item_id
   and cp.para_code1 = :PARA_CODE1
   and (cp.eparchy_code = :EPARCHY_CODE or cp.eparchy_code = 'ZZZZ')