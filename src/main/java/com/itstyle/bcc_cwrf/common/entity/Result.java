package com.itstyle.bcc_cwrf.common.entity;

import java.util.HashMap;
import java.util.Map;
/**
 * 页面响应entity
 * 创建者 张志朋
 * 创建时间	2018年3月8日
 */
public class Result extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	public Result() {
		put("code", 0);
	}

	public static Result error() {
		return error(500, "未知异常，请联系管理员");
	}

	public static Result error(String msg) {
		return error(500, msg);
	}

	public static Result error(int code, String msg) {
		Result r = new Result();
		r.put("code", code);
		r.put("msg", msg);
		return r;
	}
	public static Result error(Object msg) {
		Result r = new Result();
		r.put("msg", msg);
		return r;
	}
	public static Result ok(int code, Object msg, Object data) {
		Result r = new Result();
		r.put("code", code);
		r.put("msg", msg);
		r.put("data", data);
		return r;
	}

	public static Result ok(Object data) {
		Result r = new Result();
		r.put("code", 200);
		r.put("msg", "success");
		r.put("data", data);
		return r;
	}

	public static Result ok(Map<String, Object> map) {
		Result r = new Result();
		r.put("code", 200);
		r.put("msg", "success");
		r.putAll(map);
		return r;
	}
	public static Result qjok(Map<String, Object> map) {
		Result r = new Result();
		r.put("code", 200);
		r.put("msg", "success");
		r.put("data", map);
		return r;
	}

	public static Result ok() {
		return new Result();
	}

	@Override
	public Result put(String key, Object value) {
		super.put(key, value);
		return this;
	}
}