SELECT A.START_VALUE ,A.LOG_ID,
F_RES_GETCODENAME('res_kind_code','4','ZZZZ',A.RES_KIND_CODE) TERMINAL_CATEGORY_NAME,
F_RES_GETCODENAME('card_kind_code', A.CARD_KIND_CODE, 'ZZZZ', '') TERMINAL_TYPE_NAME,
F_RES_GETCODENAME('card_kind_code', A.BRAND_CODE, 'ZZZZ', '') BRAND_NAME,
F_RES_GETCODENAME('card_kind_code', A.MODEL_CODE, 'ZZZZ', '') MODEL_NAME
FROM TF_B_TERMINAL_ASSIGN_DETAIL A
WHERE A.LOG_ID=:LOG_ID