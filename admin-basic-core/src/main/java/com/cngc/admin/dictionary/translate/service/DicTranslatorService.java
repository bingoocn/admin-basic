package com.cngc.admin.dictionary.translate.service;

/**
 * 数据字典转换服务接口.
 * @author duanyl
 */
public interface DicTranslatorService {
    /**
     * 数据字典编码转换为显示名字.
     * @param type  数据字典类型
     * @param code  数据字典编码
     * @return  数据字典显示名字
     */
    String translateCodeToDisplayName(String type, String code);

    /**
     * 数据字典显示名字转换为编码.
     * @param type  数据字典类型
     * @param displayName   数据字典显示名字
     * @return  数据字典编码
     */
    String translateDisplayNameToCode(String type, String displayName);
}
