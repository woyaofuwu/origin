--IS_CACHE=Y 
 select b.rule_biz_id, b.rule_check_mode, b.tips_type, b.tips_info, b.right_code
        ,r.rule_id, r.script_id, r.is_revolt, r.rule_description 
        ,d.script_id, d.script_path, b.product_id, b.package_id, b.brand_code 
 from   td_bre_business b, td_bre_relation r, td_bre_definition d 
 where  b.rule_biz_id = r.rule_biz_id 
 and    r.script_id = d.script_id 
 and    sysdate between b.start_date and b.end_date 
 and    sysdate between r.start_date and r.end_date 
 and    b.rule_type_code = :RULE_BIZ_TYPE_CODE 
 and    b.rule_kind_code = :RULE_BIZ_KIND_CODE 
 and    b.rule_twig_code = :RULE_BIZ_TWIG_CODE 
 AND    (INSTR(:TIPS_TYPE,b.tips_type) > 0)
 and    (b.trade_type_code = -1 or b.trade_type_code = :TRADE_TYPE_CODE) 
 and    (b.eparchy_code = 'ZZZZ' or b.eparchy_code = :EPARCHY_CODE)
 and    (b.in_mode_code is null or instr(b.in_mode_code, :IN_MODE_CODE) > 0)
 and    (b.element_id is null or b.element_id = :ELEMENT_ID)
 and    (:RULE_EVNT_CODE is null or b.RULE_EVNT_CODE = :RULE_EVNT_CODE)
 and    (b.spec_cdtion is null and instr(nvl(b.spec_cdtion, '-'), :TRADE_TYPE_CODE) < 1) 
 order  by b.rule_biz_id, r.execute_order