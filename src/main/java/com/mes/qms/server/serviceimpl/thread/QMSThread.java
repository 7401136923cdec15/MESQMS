package com.mes.qms.server.serviceimpl.thread;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mes.qms.server.service.RSMService;
import com.mes.qms.server.service.po.bms.BMSEmployee;
import com.mes.qms.server.service.utils.DesUtil;
import com.mes.qms.server.service.utils.StringUtils;
import com.mes.qms.server.serviceimpl.RSMServiceImpl;
import com.mes.qms.server.utils.SessionContants;

@Component
public class QMSThread implements DisposableBean {
	private static final Logger logger = LoggerFactory.getLogger(QMSThread.class);
	@Autowired
	RSMService wRSMService = new RSMServiceImpl();
	private static QMSThread Instance;
	private int Ticks = 0;

	@PostConstruct
	public void init() {
		Instance = this;
		Instance.AdminUser = this.AdminUser;
		Instance.wRSMService = this.wRSMService;

		Run();
	}

	public QMSThread() {
		super();
		AdminUser.ID = -100;
		AdminUser.LoginName = DesUtil.encrypt("SHRISMCIS", SessionContants.appSecret);
		AdminUser.Password = DesUtil.encrypt("shrismcis", SessionContants.appSecret);
		AdminUser.CompanyID = 0;
	}

	private BMSEmployee AdminUser = new BMSEmployee();

	public QMSThread(BMSEmployee wLoginUser) {
		super();
		AdminUser = wLoginUser;
	}

	boolean mIsStart = false;

	private void Run() {
		try {
			if (mIsStart)
				return;
			mIsStart = true;

			// 10秒后启动线程，防止其他服务未启动，导致访问超时
			Thread.sleep(10000L);
			logger.info("QMSThread Started!!");
			new Thread(() -> {
				while (mIsStart) {
					try {
						// 睡眠
						Thread.sleep(3000);

						if (this.Ticks % 1 == 0) {
							// 自动发起转序申请
							Instance.wRSMService.RSM_AutoTurnOrder(AdminUser);
							// 自动通过转序申请单
							Instance.wRSMService.RSM_AutoPassApply(AdminUser);
							// 禁用已关闭的派工消息
							Instance.wRSMService.RSM_DisableDispatchMessage(AdminUser);
							// 自动统计今年的车辆完工数据，同步到forcas历史记录表
							Instance.wRSMService.RSM_AutoCalculateTrainHistory(AdminUser);
						}
						if (this.Ticks % 60 == 0) {
							// 自动删除导出文件夹
							Instance.wRSMService.RSM_DeleteExport(AdminUser);
							// 自动创建转序单(工位已完工，但转序单未自动生成)
							Instance.wRSMService.RSM_AutoCreateTurnOrderForm(AdminUser);
							// 自动完成工序任务(自检、互检、专检任务都已完成)
							Instance.wRSMService.RSM_AutoFinishTaskStep(AdminUser);
							// 自动修改工位任务状态(工序任务未完工，工位任务状态完工)
							Instance.wRSMService.RSM_AutoUpdateTaskPart(AdminUser);
						}

						this.Ticks++;

						if (Ticks > 1000)
							this.Ticks = 1;
					} catch (Exception ex) {
						logger.error(ex.toString());
					}
				}
			}).start();
		} catch (Exception ex) {
			logger.error(StringUtils.Format("QMS start failed error:{0}", ex.toString()));
		}
	}

	@Override
	public void destroy() throws Exception {
		mIsStart = false;
	}
}
