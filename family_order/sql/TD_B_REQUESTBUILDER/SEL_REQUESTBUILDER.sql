--IS_CACHE=Y
SELECT trade_type_code, in_mode_code, expression, class_name, '1' order_no
        FROM TD_B_REQUESTBUILDER
        WHERE STATE = '1'
        AND TRADE_TYPE_CODE = :TRADE_TYPE_CODE
        AND IN_MODE_CODE = :IN_MODE_CODE
        AND ORDER_TYPE_CODE = :ORDER_TYPE_CODE
union all
SELECT trade_type_code, in_mode_code, expression, class_name, '2' order_no
        FROM TD_B_REQUESTBUILDER
        WHERE STATE = '1'
        AND TRADE_TYPE_CODE = :TRADE_TYPE_CODE
        AND IN_MODE_CODE = '-1'
        AND ORDER_TYPE_CODE = :ORDER_TYPE_CODE
