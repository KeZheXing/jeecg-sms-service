package org.jeecg.modules.system.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Joiner;
import com.jeecg.dingtalk.api.core.response.Response;
import freemarker.core.TemplateClassResolver;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.dto.DataLogDTO;
import org.jeecg.common.api.dto.OnlineAuthDTO;
import org.jeecg.common.api.dto.message.*;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.UrlMatchEnum;
import org.jeecg.common.constant.*;
import org.jeecg.common.constant.enums.*;
import org.jeecg.common.desensitization.util.SensitiveInfoUtil;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.query.QueryCondition;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.system.vo.*;
import org.jeecg.common.util.DySmsHelper;
import org.jeecg.common.util.HTMLUtils;
import org.jeecg.common.util.YouBianCodeUtil;
import org.jeecg.common.util.dynamic.db.FreemarkerParseFactory;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.config.firewall.SqlInjection.IDictTableWhiteListHandler;
import org.jeecg.config.mybatis.MybatisPlusSaasConfig;
import org.jeecg.modules.airag.app.entity.SmsDevice;
import org.jeecg.modules.airag.app.entity.SysBalanceRecord;
import org.jeecg.modules.message.entity.SysMessageTemplate;
import org.jeecg.modules.message.handle.impl.DdSendMsgHandle;
import org.jeecg.modules.message.handle.impl.EmailSendMsgHandle;
import org.jeecg.modules.message.handle.impl.QywxSendMsgHandle;
import org.jeecg.modules.message.handle.impl.SystemSendMsgHandle;
import org.jeecg.modules.message.service.ISysMessageTemplateService;
import org.jeecg.modules.message.websocket.WebSocket;
import org.jeecg.modules.system.entity.*;
import org.jeecg.modules.system.mapper.*;
import org.jeecg.modules.system.service.*;
import org.jeecg.modules.system.util.SecurityUtil;
import org.jeecg.modules.system.vo.lowapp.SysDictVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 底层共通业务API，提供其他独立模块调用
 * @Author: scott
 * @Date:2019-4-20 
 * @Version:V1.0
 */
@Slf4j
@Service
public class SysBalanceRecordImpl extends ServiceImpl<SysBalanceRecordMapper, SysBalanceRecord> implements ISysBalanceRecordService {

	@Override
	public Result<IPage<SysBalanceRecord>> queryPageList(HttpServletRequest req, QueryWrapper<SysBalanceRecord> queryWrapper, Integer pageSize, Integer pageNo) {
		Result<IPage<SysBalanceRecord>> result = new Result<IPage<SysBalanceRecord>>();

		//TODO 外部模拟登陆临时账号，列表不显示

		Page<SysBalanceRecord> page = new Page<SysBalanceRecord>(pageNo, pageSize);
		IPage<SysBalanceRecord> pageList = this.page(page, queryWrapper);

		result.setSuccess(true);
		result.setResult(pageList);
		//log.info(pageList.toString());
		return result;
	}
}