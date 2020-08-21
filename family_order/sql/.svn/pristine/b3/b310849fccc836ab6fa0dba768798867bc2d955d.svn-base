SELECT *
  FROM tf_f_user_purchase a
 WHERE a.user_id = :USER_ID
   AND a.purchase_attr = :PURCHASE_ATTR
   AND a.purchase_mode = :PURCHASE_MODE
   AND EXISTS (SELECT 1 FROM td_b_purchasetrade b 
                WHERE b.purchase_attr = a.purchase_attr
                  AND b.purchase_mode = a.purchase_mode
                  AND b.eparchy_code = :EPARCHY_CODE
                  AND substr(b.para_code1,12,1) = '1')