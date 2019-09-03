package com.cngc.admin.dictionary.translate.utils;

import com.cngc.admin.dictionary.translate.service.DicTranslatorService;
import com.cngc.boot.core.util.SpringContextUtil;

/**
 * DictTranslatorService代理类.
 *
 * @author duanyl
 */
public class DictTranslatorServiceProxy {
    private static volatile DicTranslatorService dicTranslatorService;

    private DictTranslatorServiceProxy() {

    }

    public static DicTranslatorService getInstance() {
        if(dicTranslatorService == null) {
            synchronized (DictTranslatorServiceProxy.class) {
                if(dicTranslatorService == null) {
                    dicTranslatorService = SpringContextUtil.getBean(DicTranslatorService.class);
                }
            }
        }
        return dicTranslatorService;
    }

}
