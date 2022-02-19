package com.mes.qms.server.service.po.aps;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.mes.qms.server.service.utils.StringUtils;

/**
 * 日计划 备注
 * 
 * @author ShrisJava
 *
 */
public class APSTaskRemark implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 提交人
	 */
	public int SubmitID = 0;
	/**
	 * 提交时刻
	 */
	public Calendar SubimtTime = Calendar.getInstance();
	/**
	 * 备注
	 */
	public String Remark = "";

	public APSTaskRemark() {
		SubimtTime.set(2000, 0, 1);
	}

	public String toString() {
		SimpleDateFormat wSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String wTime = wSDF.format(SubimtTime.getTime());

		return StringUtils.Format("{0}+|:|+{1}+|:|+{2}",
				new Object[] { Integer.valueOf(this.SubmitID), wTime, Remark });
	}

	public APSTaskRemark(String wValue) {
		try {
			if (StringUtils.isEmpty(wValue))
				return;
			List<String> wArray = StringUtils.splitList(wValue, "+|:|+");
			if (wArray == null || wArray.size() <= 0) {
				return;
			}
			if (wArray.size() > 0) {
				this.SubmitID = StringUtils.parseInt(wArray.get(0)).intValue();
			}
			if (wArray.size() > 1) {
				SimpleDateFormat wSDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date wDate = wSDF.parse(StringUtils.parseString(wArray.get(1)));
				this.SubimtTime.setTime(wDate);
			}
			if (wArray.size() > 2) {
				this.Remark = StringUtils.parseString(wArray.get(2));
			}
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}

	public static String ListToString(List<APSTaskRemark> wItems) {
		String wResult = "";
		if (wItems == null || wItems.size() <= 0)
			return wResult;
		List<String> wResultList = new ArrayList<>();
		for (APSTaskRemark wAPSTaskRemark : wItems) {
			if (wAPSTaskRemark == null)
				continue;
			wResultList.add(wAPSTaskRemark.toString());
		}
		wResult = StringUtils.Join("+|;|+", wResultList);

		return wResult;
	}

	public static List<APSTaskRemark> StringToList(String wStringValues) {
		List<APSTaskRemark> wResult = new ArrayList<>();

		if (StringUtils.isEmpty(wStringValues)) {
			return wResult;
		}

		List<String> wResultList = StringUtils.splitList(wStringValues, "+|;|+");
		for (String wValue : wResultList) {
			if (StringUtils.isEmpty(wValue))
				continue;
			wResult.add(new APSTaskRemark(wValue));
		}
		return wResult;
	}
}
