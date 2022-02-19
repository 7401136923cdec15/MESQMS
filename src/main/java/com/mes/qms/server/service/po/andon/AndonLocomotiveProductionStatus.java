package com.mes.qms.server.service.po.andon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 广州电力机车生产状态
 * 
 * @author YouWang·Peng
 * @CreateTime 2021-6-23 09:18:07
 */
public class AndonLocomotiveProductionStatus implements Serializable {
	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	// 停时
	/**
	 * C6验后停时指标
	 */
	public int YHTS_C6_ZB = 0;
	/**
	 * C6验后停时
	 */
	public double YHTS_C6 = 0;
	/**
	 * C5验后停时指标
	 */
	public int YHTS_C5_ZB = 0;
	/**
	 * C5验后停时
	 */
	public double YHTS_C5 = 0;
	/**
	 * C6检修停时指标
	 */
	public int JCTS_C6_ZB = 0;
	/**
	 * C6检修停时
	 */
	public double JCTS_C6 = 0;
	/**
	 * C5检修停时指标
	 */
	public int JCTS_C5_ZB = 0;
	/**
	 * C5检修停时
	 */
	public double JCTS_C5 = 0;

	// 当月过程返工件数
	/**
	 * 一工区返工件数指标
	 */
	public int FGJS_One_ZB = 0;
	/**
	 * 一工区返工件数
	 */
	public int FGJS_One = 0;
	/**
	 * 二工区返工件数指标
	 */
	public int FGJS_Two_ZB = 0;
	/**
	 * 二工区返工件数
	 */
	public int FGJS_Two = 0;
	/**
	 * 三工区返工件数指标
	 */
	public int FGJS_Three_ZB = 0;
	/**
	 * 三工区返工件数
	 */
	public int FGJS_Three = 0;
	/**
	 * 四工区返工件数指标
	 */
	public int FGJS_Four_ZB = 0;
	/**
	 * 四工区返工件数
	 */
	public int FGJS_Four = 0;

	// 机车统计数据
	/**
	 * 全年累计修竣
	 */
	public int LJXJ_Year = 0;
	/**
	 * 全年C6修累计修竣
	 */
	public int LJXJ_Year_C6 = 0;
	/**
	 * 全年C5修累计修竣
	 */
	public int LJXJ_Year_C5 = 0;
	/**
	 * 本月计划交付
	 */
	public int JHJF_Month = 0;
	/**
	 * 本月C6修计划交付
	 */
	public int JHJF_Month_C6 = 0;
	/**
	 * 本月C5修计划交付
	 */
	public int JHJF_Month_C5 = 0;
	/**
	 * 本月实际交付
	 */
	public int SJJF_Month = 0;
	/**
	 * 本月C6修实际交付
	 */
	public int SJJF_Month_C6 = 0;
	/**
	 * 本月C5修实际交付
	 */
	public int SJJF_Month_C5 = 0;

	// 视频、图片、PPT地址
	/**
	 * 视频、图片、PPT地址
	 */
	public String Uri = "";
	/**
	 * 播放类型 1视频 2异常滚动
	 */
	public int Type = 0;

	// 完成进度
	/**
	 * 一工区未完成数
	 */
	public int NotDone_One = 0;
	/**
	 * 一工区完成数
	 */
	public int Done_One = 0;
	/**
	 * 二工区未完成数
	 */
	public int NotDone_Two = 0;
	/**
	 * 二工区完成数
	 */
	public int Done_Two = 0;
	/**
	 * 三工区未完成数
	 */
	public int NotDone_Three = 0;
	/**
	 * 三工区完成数
	 */
	public int Done_Three = 0;
	/**
	 * 四工区未完成数
	 */
	public int NotDone_Four = 0;
	/**
	 * 四工区完成数
	 */
	public int Done_Four = 0;

	// 不合格评审、异常情况
	/**
	 * 不合格评审单本月总计
	 */
	public int Total_Ncr = 0;
	/**
	 * 不合格评审单处理中
	 */
	public int Doing_Ncr = 0;
	/**
	 * 异常单本月总计
	 */
	public int Total_Exc = 0;
	/**
	 * 异常单处理中
	 */
	public int Doing_Exc = 0;

	// 零公里得分
	/**
	 * 1月
	 */
	public int Grade_1 = 0;
	public int Grade_2 = 0;
	public int Grade_3 = 0;
	public int Grade_4 = 0;
	public int Grade_5 = 0;
	public int Grade_6 = 0;
	public int Grade_7 = 0;
	public int Grade_8 = 0;
	public int Grade_9 = 0;
	public int Grade_10 = 0;
	public int Grade_11 = 0;
	public int Grade_12 = 0;
	/**
	 * 得分合格数
	 */
	public int Grade_Quality = 0;
	/**
	 * 零公里得分启用
	 */
	public int GradeFlag = 0;
	/**
	 * 零公里得分数据集合
	 */
	public List<Integer> GradeList = new ArrayList<Integer>();

	public AndonLocomotiveProductionStatus() {
	}

	public AndonLocomotiveProductionStatus(int yHTS_C6_ZB, double yHTS_C6, int yHTS_C5_ZB, double yHTS_C5,
			int jCTS_C6_ZB, double jCTS_C6, int jCTS_C5_ZB, double jCTS_C5, int fGJS_One_ZB, int fGJS_One,
			int fGJS_Two_ZB, int fGJS_Two, int fGJS_Three_ZB, int fGJS_Three, int fGJS_Four_ZB, int fGJS_Four,
			int lJXJ_Year, int lJXJ_Year_C6, int lJXJ_Year_C5, int jHJF_Month, int jHJF_Month_C6, int jHJF_Month_C5,
			int sJJF_Month, int sJJF_Month_C6, int sJJF_MOnth_C5, String uri, int type, int notDone_One, int done_One,
			int notDone_Two, int done_Two, int notDone_Three, int done_Three, int notDone_Four, int done_Four,
			int total_Ncr, int doing_Ncr, int total_Exc, int doing_Exc) {
		super();
		YHTS_C6_ZB = yHTS_C6_ZB;
		YHTS_C6 = yHTS_C6;
		YHTS_C5_ZB = yHTS_C5_ZB;
		YHTS_C5 = yHTS_C5;
		JCTS_C6_ZB = jCTS_C6_ZB;
		JCTS_C6 = jCTS_C6;
		JCTS_C5_ZB = jCTS_C5_ZB;
		JCTS_C5 = jCTS_C5;
		FGJS_One_ZB = fGJS_One_ZB;
		FGJS_One = fGJS_One;
		FGJS_Two_ZB = fGJS_Two_ZB;
		FGJS_Two = fGJS_Two;
		FGJS_Three_ZB = fGJS_Three_ZB;
		FGJS_Three = fGJS_Three;
		FGJS_Four_ZB = fGJS_Four_ZB;
		FGJS_Four = fGJS_Four;
		LJXJ_Year = lJXJ_Year;
		LJXJ_Year_C6 = lJXJ_Year_C6;
		LJXJ_Year_C5 = lJXJ_Year_C5;
		JHJF_Month = jHJF_Month;
		JHJF_Month_C6 = jHJF_Month_C6;
		JHJF_Month_C5 = jHJF_Month_C5;
		SJJF_Month = sJJF_Month;
		SJJF_Month_C6 = sJJF_Month_C6;
		SJJF_Month_C5 = sJJF_MOnth_C5;
		Uri = uri;
		Type = type;
		NotDone_One = notDone_One;
		Done_One = done_One;
		NotDone_Two = notDone_Two;
		Done_Two = done_Two;
		NotDone_Three = notDone_Three;
		Done_Three = done_Three;
		NotDone_Four = notDone_Four;
		Done_Four = done_Four;
		Total_Ncr = total_Ncr;
		Doing_Ncr = doing_Ncr;
		Total_Exc = total_Exc;
		Doing_Exc = doing_Exc;
	}

	public int getYHTS_C6_ZB() {
		return YHTS_C6_ZB;
	}

	public double getYHTS_C6() {
		return YHTS_C6;
	}

	public int getSJJF_Month_C5() {
		return SJJF_Month_C5;
	}

	public int getGrade_1() {
		return Grade_1;
	}

	public int getGrade_2() {
		return Grade_2;
	}

	public int getGrade_3() {
		return Grade_3;
	}

	public int getGrade_4() {
		return Grade_4;
	}

	public int getGrade_5() {
		return Grade_5;
	}

	public int getGrade_6() {
		return Grade_6;
	}

	public int getGrade_7() {
		return Grade_7;
	}

	public int getGrade_8() {
		return Grade_8;
	}

	public int getGrade_9() {
		return Grade_9;
	}

	public int getGrade_10() {
		return Grade_10;
	}

	public int getGrade_11() {
		return Grade_11;
	}

	public int getGrade_12() {
		return Grade_12;
	}

	public int getGrade_Quality() {
		return Grade_Quality;
	}

	public int getGradeFlag() {
		return GradeFlag;
	}

	public void setSJJF_Month_C5(int sJJF_Month_C5) {
		SJJF_Month_C5 = sJJF_Month_C5;
	}

	public void setGrade_1(int grade_1) {
		Grade_1 = grade_1;
	}

	public void setGrade_2(int grade_2) {
		Grade_2 = grade_2;
	}

	public void setGrade_3(int grade_3) {
		Grade_3 = grade_3;
	}

	public void setGrade_4(int grade_4) {
		Grade_4 = grade_4;
	}

	public void setGrade_5(int grade_5) {
		Grade_5 = grade_5;
	}

	public void setGrade_6(int grade_6) {
		Grade_6 = grade_6;
	}

	public void setGrade_7(int grade_7) {
		Grade_7 = grade_7;
	}

	public void setGrade_8(int grade_8) {
		Grade_8 = grade_8;
	}

	public void setGrade_9(int grade_9) {
		Grade_9 = grade_9;
	}

	public void setGrade_10(int grade_10) {
		Grade_10 = grade_10;
	}

	public void setGrade_11(int grade_11) {
		Grade_11 = grade_11;
	}

	public void setGrade_12(int grade_12) {
		Grade_12 = grade_12;
	}

	public void setGrade_Quality(int grade_Quality) {
		Grade_Quality = grade_Quality;
	}

	public void setGradeFlag(int gradeFlag) {
		GradeFlag = gradeFlag;
	}

	public int getYHTS_C5_ZB() {
		return YHTS_C5_ZB;
	}

	public double getYHTS_C5() {
		return YHTS_C5;
	}

	public int getJCTS_C6_ZB() {
		return JCTS_C6_ZB;
	}

	public double getJCTS_C6() {
		return JCTS_C6;
	}

	public int getJCTS_C5_ZB() {
		return JCTS_C5_ZB;
	}

	public double getJCTS_C5() {
		return JCTS_C5;
	}

	public int getFGJS_One_ZB() {
		return FGJS_One_ZB;
	}

	public int getFGJS_One() {
		return FGJS_One;
	}

	public int getFGJS_Two_ZB() {
		return FGJS_Two_ZB;
	}

	public int getFGJS_Two() {
		return FGJS_Two;
	}

	public int getFGJS_Three_ZB() {
		return FGJS_Three_ZB;
	}

	public int getFGJS_Three() {
		return FGJS_Three;
	}

	public int getFGJS_Four_ZB() {
		return FGJS_Four_ZB;
	}

	public int getFGJS_Four() {
		return FGJS_Four;
	}

	public int getLJXJ_Year() {
		return LJXJ_Year;
	}

	public int getLJXJ_Year_C6() {
		return LJXJ_Year_C6;
	}

	public int getLJXJ_Year_C5() {
		return LJXJ_Year_C5;
	}

	public int getJHJF_Month() {
		return JHJF_Month;
	}

	public int getJHJF_Month_C6() {
		return JHJF_Month_C6;
	}

	public int getJHJF_Month_C5() {
		return JHJF_Month_C5;
	}

	public int getSJJF_Month() {
		return SJJF_Month;
	}

	public int getSJJF_Month_C6() {
		return SJJF_Month_C6;
	}

	public int getSJJF_MOnth_C5() {
		return SJJF_Month_C5;
	}

	public String getUri() {
		return Uri;
	}

	public int getType() {
		return Type;
	}

	public int getNotDone_One() {
		return NotDone_One;
	}

	public int getDone_One() {
		return Done_One;
	}

	public int getNotDone_Two() {
		return NotDone_Two;
	}

	public int getDone_Two() {
		return Done_Two;
	}

	public int getNotDone_Three() {
		return NotDone_Three;
	}

	public int getDone_Three() {
		return Done_Three;
	}

	public int getNotDone_Four() {
		return NotDone_Four;
	}

	public int getDone_Four() {
		return Done_Four;
	}

	public int getTotal_Ncr() {
		return Total_Ncr;
	}

	public int getDoing_Ncr() {
		return Doing_Ncr;
	}

	public int getTotal_Exc() {
		return Total_Exc;
	}

	public int getDoing_Exc() {
		return Doing_Exc;
	}

	public void setYHTS_C6_ZB(int yHTS_C6_ZB) {
		YHTS_C6_ZB = yHTS_C6_ZB;
	}

	public void setYHTS_C6(double yHTS_C6) {
		YHTS_C6 = yHTS_C6;
	}

	public void setYHTS_C5_ZB(int yHTS_C5_ZB) {
		YHTS_C5_ZB = yHTS_C5_ZB;
	}

	public void setYHTS_C5(double yHTS_C5) {
		YHTS_C5 = yHTS_C5;
	}

	public void setJCTS_C6_ZB(int jCTS_C6_ZB) {
		JCTS_C6_ZB = jCTS_C6_ZB;
	}

	public void setJCTS_C6(double jCTS_C6) {
		JCTS_C6 = jCTS_C6;
	}

	public void setJCTS_C5_ZB(int jCTS_C5_ZB) {
		JCTS_C5_ZB = jCTS_C5_ZB;
	}

	public void setJCTS_C5(double jCTS_C5) {
		JCTS_C5 = jCTS_C5;
	}

	public void setFGJS_One_ZB(int fGJS_One_ZB) {
		FGJS_One_ZB = fGJS_One_ZB;
	}

	public void setFGJS_One(int fGJS_One) {
		FGJS_One = fGJS_One;
	}

	public void setFGJS_Two_ZB(int fGJS_Two_ZB) {
		FGJS_Two_ZB = fGJS_Two_ZB;
	}

	public void setFGJS_Two(int fGJS_Two) {
		FGJS_Two = fGJS_Two;
	}

	public void setFGJS_Three_ZB(int fGJS_Three_ZB) {
		FGJS_Three_ZB = fGJS_Three_ZB;
	}

	public void setFGJS_Three(int fGJS_Three) {
		FGJS_Three = fGJS_Three;
	}

	public void setFGJS_Four_ZB(int fGJS_Four_ZB) {
		FGJS_Four_ZB = fGJS_Four_ZB;
	}

	public void setFGJS_Four(int fGJS_Four) {
		FGJS_Four = fGJS_Four;
	}

	public void setLJXJ_Year(int lJXJ_Year) {
		LJXJ_Year = lJXJ_Year;
	}

	public void setLJXJ_Year_C6(int lJXJ_Year_C6) {
		LJXJ_Year_C6 = lJXJ_Year_C6;
	}

	public void setLJXJ_Year_C5(int lJXJ_Year_C5) {
		LJXJ_Year_C5 = lJXJ_Year_C5;
	}

	public void setJHJF_Month(int jHJF_Month) {
		JHJF_Month = jHJF_Month;
	}

	public void setJHJF_Month_C6(int jHJF_Month_C6) {
		JHJF_Month_C6 = jHJF_Month_C6;
	}

	public void setJHJF_Month_C5(int jHJF_Month_C5) {
		JHJF_Month_C5 = jHJF_Month_C5;
	}

	public void setSJJF_Month(int sJJF_Month) {
		SJJF_Month = sJJF_Month;
	}

	public void setSJJF_Month_C6(int sJJF_Month_C6) {
		SJJF_Month_C6 = sJJF_Month_C6;
	}

	public void setSJJF_MOnth_C5(int sJJF_MOnth_C5) {
		SJJF_Month_C5 = sJJF_MOnth_C5;
	}

	public void setUri(String uri) {
		Uri = uri;
	}

	public void setType(int type) {
		Type = type;
	}

	public void setNotDone_One(int notDone_One) {
		NotDone_One = notDone_One;
	}

	public void setDone_One(int done_One) {
		Done_One = done_One;
	}

	public void setNotDone_Two(int notDone_Two) {
		NotDone_Two = notDone_Two;
	}

	public void setDone_Two(int done_Two) {
		Done_Two = done_Two;
	}

	public void setNotDone_Three(int notDone_Three) {
		NotDone_Three = notDone_Three;
	}

	public void setDone_Three(int done_Three) {
		Done_Three = done_Three;
	}

	public void setNotDone_Four(int notDone_Four) {
		NotDone_Four = notDone_Four;
	}

	public void setDone_Four(int done_Four) {
		Done_Four = done_Four;
	}

	public void setTotal_Ncr(int total_Ncr) {
		Total_Ncr = total_Ncr;
	}

	public void setDoing_Ncr(int doing_Ncr) {
		Doing_Ncr = doing_Ncr;
	}

	public void setTotal_Exc(int total_Exc) {
		Total_Exc = total_Exc;
	}

	public void setDoing_Exc(int doing_Exc) {
		Doing_Exc = doing_Exc;
	}

	public List<Integer> getGradeList() {
		return GradeList;
	}

	public void setGradeList(List<Integer> gradeList) {
		GradeList = gradeList;
	}
}
