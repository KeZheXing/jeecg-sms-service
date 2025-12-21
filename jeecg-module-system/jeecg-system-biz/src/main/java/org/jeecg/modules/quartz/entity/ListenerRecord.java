package org.jeecg.modules.quartz.entity;

import lombok.Data;

@Data
public class ListenerRecord {

	/**
	 * 监听id
	 */
	private Integer recordId;

	/**
	 * 项目id
	 */
	private String transactionId;

	/**
	 * 是否确认
	 */
	private Boolean confirmed;

	private String contractRet;

	private String finalResult;

	/**
	 * 事件类型
	 */
	private String eventType;

	private String contractType;

	private String contractAddress;

	private String fromAddress;

	private String toAddress;

	private Integer matchStatus;

	private String tokenId;

	private String tokenAbbr;

	private String tokenName;

	private Integer tokenDecimal;

	private Long quant;

	private Integer rechargeRecordId;
}
