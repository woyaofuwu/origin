SELECT COUNT(*) RECORDCOUNT FROM TD_S_PRODUCTLIMIT a
 WHERE (a.PRODUCT_ID_A IN
       (SELECT PRODUCT_ID FROM TD_B_PRODUCT_RELATION
         WHERE RELATION_TYPE_CODE IN
               (SELECT b.RELATION_TYPE_CODE FROM TF_F_RELATION_UU b ,td_s_relation r
                 WHERE b.user_id_B = :USER_ID
                   AND b.RELATION_TYPE_CODE =r.relation_type_code
                   And r.relation_kind ='F'
                   AND b.ROLE_CODE_B = '1'
                   AND b.END_DATE >= LAST_DAY(TRUNC(SYSDATE)) + 1))
   AND a.PRODUCT_ID_B = :PRODUCT_ID)
    OR (NOT EXISTS (SELECT * FROM TF_F_RELATION_UU c     ,td_s_relation r
                     WHERE c.user_id_B = :USER_ID
                       AND c.RELATION_TYPE_CODE =r.relation_type_code
                       And r.relation_kind ='F'
                       AND c.ROLE_CODE_B = '1'
                       AND c.END_DATE >= LAST_DAY(TRUNC(SYSDATE)) + 1) )