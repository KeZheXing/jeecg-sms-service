package org.jeecg.common.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jeecg.common.constant.CommonConstant;

import java.io.Serializable;

/**
 *   接口返回数据格式
 * @author scott
 * @email jeecgos@163.com
 * @date  2019年1月19日
 */
@Data
@Schema(description="接口返回对象")
public class ResultApi<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 成功标志
	 */
	@Schema(description = "成功标志")
	private boolean success = true;

	/**
	 * 返回处理消息
	 */
	@Schema(description = "返回处理消息")
	private String message = "";

	private String status;

	/**
	 * 返回代码
	 */
	@Schema(description = "返回代码")
	private Integer code = 0;

	/**
	 * 返回数据对象 data
	 */
	@Schema(description = "返回数据对象")
	private T result;

	/**
	 * 时间戳
	 */
	@Schema(description = "时间戳")
	private long timestamp = System.currentTimeMillis();

	public ResultApi() {
	}

    /**
     * 兼容VUE3版token失效不跳转登录页面
     * @param code
     * @param message
     */
	public ResultApi(Integer code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public ResultApi<T> success(String message) {
		this.message = message;
		this.code = CommonConstant.SC_OK_200;
		this.success = true;
		return this;
	}

	public static<T> ResultApi<T> ok() {
		ResultApi<T> r = new ResultApi<T>();
		r.setSuccess(true);
		r.setCode(CommonConstant.SC_OK_200);
		return r;
	}

	public static<T> ResultApi<T> ok(String msg) {
		ResultApi<T> r = new ResultApi<T>();
		r.setSuccess(true);
		r.setCode(CommonConstant.SC_OK_200);
		//Result OK(String msg)方法会造成兼容性问题 issues/I4IP3D
		r.setResult((T) msg);
		r.setMessage(msg);
		return r;
	}

	public static<T> ResultApi<T> ok(T data) {
		ResultApi<T> r = new ResultApi<T>();
		r.setSuccess(true);
		r.setCode(CommonConstant.SC_OK_200);
		r.setResult(data);
		return r;
	}

	public static<T> ResultApi<T> OK() {
		ResultApi<T> r = new ResultApi<T>();
		r.setSuccess(true);
		r.setCode(CommonConstant.SC_OK_200);
		return r;
	}

	/**
	 * 此方法是为了兼容升级所创建
	 *
	 * @param msg
	 * @param <T>
	 * @return
	 */
	public static<T> ResultApi<T> OK(String msg) {
		ResultApi<T> r = new ResultApi<T>();
		r.setSuccess(true);
		r.setCode(CommonConstant.SC_OK_200);
		r.setMessage(msg);
		//Result OK(String msg)方法会造成兼容性问题 issues/I4IP3D
		r.setResult((T) msg);
		return r;
	}

	public static<T> ResultApi<T> OK(T data) {
		ResultApi<T> r = new ResultApi<T>();
		r.setSuccess(true);
		r.setCode(CommonConstant.SC_OK_200);
		r.setResult(data);
		return r;
	}

	public static<T> ResultApi<T> OK(String msg, T data) {
		ResultApi<T> r = new ResultApi<T>();
		r.setSuccess(true);
		r.setCode(CommonConstant.SC_OK_200);
		r.setMessage(msg);
		r.setResult(data);
		return r;
	}

	public static<T> ResultApi<T> error(String msg, T data) {
		ResultApi<T> r = new ResultApi<T>();
		r.setSuccess(false);
		r.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
		r.setMessage(msg);
		r.setResult(data);
		return r;
	}

	public static<T> ResultApi<T> error(String msg) {
		ResultApi<T> error = error(CommonConstant.SC_INTERNAL_SERVER_ERROR_500, msg);
		error.setStatus("error");
		return error;
	}
	
	public static<T> ResultApi<T> error(int code, String msg) {
		ResultApi<T> r = new ResultApi<T>();
		r.setCode(code);
		r.setMessage(msg);
		r.setSuccess(false);
		return r;
	}

	public ResultApi<T> error500(String message) {
		this.message = message;
		this.code = CommonConstant.SC_INTERNAL_SERVER_ERROR_500;
		this.success = false;
		return this;
	}

	/**
	 * 无权限访问返回结果
	 */
	public static<T> ResultApi<T> noauth(String msg) {
		return error(CommonConstant.SC_JEECG_NO_AUTHZ, msg);
	}

	@JsonIgnore
	private String onlTable;

}