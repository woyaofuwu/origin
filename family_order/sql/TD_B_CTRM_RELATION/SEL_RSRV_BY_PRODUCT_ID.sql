select t.* from TD_B_CTRM_RELATION t  
 WHERE t.ctrm_product_id=:CTRM_PRODUCT_ID
AND (t.EPARCHY_CODE = :EPARCHY_CODE or t.EPARCHY_CODE = 'ZZZZ')
 and sysdate between t.start_date and t.end_date 