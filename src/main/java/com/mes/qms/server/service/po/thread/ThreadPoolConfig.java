//package com.mes.qms.server.service.po.thread;
//
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * 数据收集配置，主要作用在于Spring启动时自动加载一个ExecutorService对象.
// * 
// * @author 7401136923cdec15
// * @date 2020-9-2 15:25:27
// */
//@Configuration
//public class ThreadPoolConfig {
//	
//	// 线程池
//	public static ExecutorService mExecutorService = ThreadPoolConfig.getThreadPool();
//	
//	@Bean
//	private static ExecutorService getThreadPool() {
//		return Executors.newFixedThreadPool(10);
//	}
//}
