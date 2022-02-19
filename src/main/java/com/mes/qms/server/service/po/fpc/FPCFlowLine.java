package com.mes.qms.server.service.po.fpc;

import java.io.Serializable;

/**
 * 流程图线信息
 */
public class FPCFlowLine implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	public FPCFlowPoint anode = new FPCFlowPoint();
	public FPCFlowPoint bnode = new FPCFlowPoint();

	public FPCFlowPoint getAnode() {
		return anode;
	}

	public FPCFlowPoint getBnode() {
		return bnode;
	}

	public void setAnode(FPCFlowPoint anode) {
		this.anode = anode;
	}

	public void setBnode(FPCFlowPoint bnode) {
		this.bnode = bnode;
	}
}
