package com.mes.qms.server.service.po.ipt;

import java.io.FileNotFoundException;

import org.springframework.util.ResourceUtils;

import com.mes.qms.server.service.utils.XmlTool;

public class IPTConstants {

	public IPTConstants() {
	}

	private static String ConfigPath = null;

	public static synchronized String getConfigPath() {
		if (ConfigPath == null) {
			try {
				ConfigPath = ResourceUtils.getURL("classpath:config").getPath().replace("%20", " ");
				if (ConfigPath != null && ConfigPath.length() > 3 && ConfigPath.indexOf(":") > 0) {
					if (ConfigPath.indexOf("/") == 0)
						ConfigPath = ConfigPath.substring(1);

					if (!ConfigPath.endsWith("/"))
						ConfigPath = ConfigPath + "/";
				}
			} catch (FileNotFoundException e) {
				return "config/";
			}
		}
		return ConfigPath;
	}

	private static IPTConfigs IPTConfigsObject;

	public static synchronized IPTConfigs getIPTConfigsObject() {
		if (IPTConfigsObject == null) {
			IPTConfigsObject = XmlTool.ReadXml(getConfigPath() + "IPTConfig.xml");
			if (IPTConfigsObject == null) {
				IPTConfigsObject = new IPTConfigs();
				XmlTool.SaveXml(getConfigPath() + "IPTConfig.xml", IPTConfigsObject);
			}
		}

		return IPTConfigsObject;
	}

	public static synchronized void setIPTConfigsObject(IPTConfigs iPTConfigs) {
		IPTConfigsObject = iPTConfigs;
		XmlTool.SaveXml(getConfigPath() + "IPTConfig.xml", IPTConfigsObject);

	}

	/**
	 * 预检申请项流程ID
	 */
	public static int mItemApplyFlowID = 4;
	/**
	 * 预检申请项流程类型
	 */
	public static int mItemApplyFlowType = 4;
	/**
	 * 预检流程ID
	 */
	public static int mPreCheckTaskFlowID = 5;
	/**
	 * 预检流程类型
	 */
	public static int mPreCheckTaskFlowType = 5;
	/**
	 * 预检问题项流程ID
	 */
	public static int mPreCheckProblemFlowID = 6;
	/**
	 * 预检问题项流程类型
	 */
	public static int mPreCheckProblemFlowType = 6;
}
