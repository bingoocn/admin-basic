package com.cngc.admin.utils;

public class Constants {

	public static final String EMPTY = "";
	public static final String TENEMENT_REQUEST_HEADER_NAME = "orgCode";

	/**
	 * 是否启用
	 */
	public static final String IS_ENABLE_Y = "1";//是
	public static final String IS_ENABLE_N = "0";//否

	/**
	 * 是否系统内置
	 */
	public static final String IS_SYS_BUILT_IN_Y = "1";//是
	public static final String IS_SYS_BUILT_IN_N = "0";//否

	/**
	 * 是否可编辑
	 */
	public static final String IS_CAN_UPDATE_Y = "1";//是
	public static final String IS_CAN_UPDATE_N = "0";//否

	/**
	 * 是否可拓展
	 */
	public static final String IS_CAN_EXT_Y = "1";//是
	public static final String IS_CAN_EXT_N = "0";//否

	/**
	 * 是否已被逻辑删除
	 */
	public static final String IS_DEL_Y = "1";//是
	public static final String IS_DEL_N = "0";//否

	/**
	 * 数据密级默认值
	 */
	public static final String SECRET_LEVEL = "01";

	/**
	 * 数据类型01：系统、02：菜单、03：资源、04：组织
	 */
	public static final String SYSTEM_TYPE = "01";
	public static final String MENU_TYPE = "02";
	public static final String RESOURCE_TYPE = "03";
	public static final String ORG_TYPE = "04";

	/**
	 * 访问对象类型数组-01：系统、02：菜单、03：资源
	 */
	public static final String[] OBJECT_TYPE = {"01", "02", "03"};

	/**
	 * 业务系统虚拟根节点ID
	 */
	public static final String SYSTEMROOT_ID = "1";

	/**
	 * 数据字典虚拟根节点ID
	 */
	public static final String DATADICTIONARYROOT_ID = "1";

}