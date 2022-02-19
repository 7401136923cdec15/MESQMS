package com.mes.qms.server.service.po.ipt;

import com.mes.qms.server.service.po.ipt.IPTProblemBomItem;
import com.mes.qms.server.service.utils.StringUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IPTProblemBomItem implements Serializable {
	private static final long serialVersionUID = 1L;
	public int BOMID = 0;

	public String MaterialNo = "";

	public String MaterialName = "";

	public int Number = 0;

	public IPTProblemBomItem() {
	}

	public String toString() {
		return StringUtils.Format("{0}+|:|+{1}+|:|+{2}+|:|+{3}", new Object[] { Integer.valueOf(this.BOMID),
				this.MaterialNo, this.MaterialName, Integer.valueOf(this.Number) });
	}

	public IPTProblemBomItem(String wValue) {
		if (StringUtils.isEmpty(wValue))
			return;
		List<String> wArray = StringUtils.splitList(wValue, "+|:|+");
		if (wArray == null || wArray.size() <= 0) {
			return;
		}
		if (wArray.size() > 0) {
			this.BOMID = StringUtils.parseInt(wArray.get(0)).intValue();
		}
		if (wArray.size() > 1) {
			this.MaterialNo = StringUtils.parseString(wArray.get(1));
		}
		if (wArray.size() > 2) {
			this.MaterialName = StringUtils.parseString(wArray.get(2));
		}
		if (wArray.size() > 3) {
			this.Number = StringUtils.parseInt(wArray.get(3)).intValue();
		}
	}

	public static String ListToString(List<IPTProblemBomItem> wItems) {
		String wResult = "";
		if (wItems == null || wItems.size() <= 0)
			return wResult;
		List<String> wResultList = new ArrayList<>();
		for (IPTProblemBomItem iptProblemBomItem : wItems) {
			if (iptProblemBomItem == null)
				continue;
			wResultList.add(iptProblemBomItem.toString());
		}
		wResult = StringUtils.Join("+|;|+", wResultList);

		return wResult;
	}

	public static List<IPTProblemBomItem> StringToList(String wStringValues) {
		List<IPTProblemBomItem> wResult = new ArrayList<>();

		if (StringUtils.isEmpty(wStringValues)) {
			return wResult;
		}

		List<String> wResultList = StringUtils.splitList(wStringValues, "+|;|+");
		for (String wValue : wResultList) {
			if (StringUtils.isEmpty(wValue))
				continue;
			wResult.add(new IPTProblemBomItem(wValue));
		}
		return wResult;
	}
}

/*
 * Location: C:\Users\Shris\Desktop\新建文件夹
 * (5)\MESQMS(1).war!\WEB-INF\classes\com\mes\qms\server\service\po\ipt\
 * IPTProblemBomItem.class Java compiler version: 8 (52.0) JD-Core Version:
 * 1.1.2
 */