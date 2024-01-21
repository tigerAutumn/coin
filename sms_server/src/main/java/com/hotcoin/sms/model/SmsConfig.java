package com.hotcoin.sms.model;

import java.io.Serializable;

/**
 * 短信信息
 * @author XXX
 *
 */
public class SmsConfig implements Serializable, Comparable<SmsConfig> {
	    /**短信提供商类名*/
	    private String smsclazz;
	    /**是否激活 1:激活 0:失效 */
		private Long isactivity;
	    /***/
	    private String action;
	    /***/
	    private Long priority;
	    /***/
	    private String description;

		public String getSmsclazz() {
			return smsclazz;
		}
		public void setSmsclazz(String smsclazz) {
			this.smsclazz = smsclazz;
		}
		public Long getIsactivity() {
			return isactivity;
		}
		public void setIsactivity(Long isactivity) {
			this.isactivity = isactivity;
		}
		public String getAction() {
			return action;
		}
		public void setAction(String action) {
			this.action = action;
		}
		public Long getPriority() {
			return priority;
		}
		public void setPriority(Long priority) {
			this.priority = priority;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("SmsConfig").append("[ ");
			sb.append("smsclazz=").append(smsclazz).append(", ");
			sb.append("isactivity=").append(isactivity).append(", ");
			sb.append("action=").append(action).append(", ");
			sb.append("priority=").append(priority).append(", ");
			sb.append("description=").append(description).append(", ");
			sb.replace(sb.length() - 2, sb.length(), " ]");
			return sb.toString();
		}

	@Override
	public int compareTo(SmsConfig o) {
		return this.priority.compareTo(o.getPriority());
	}
}
