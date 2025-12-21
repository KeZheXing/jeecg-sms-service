package org.jeecg.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.CommonAPI;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.util.TokenUtils;
import org.jeecg.modules.airag.app.entity.SysBalanceRecord;
import org.jeecg.modules.airag.app.entity.SysStatisticsCost;
import org.jeecg.modules.system.mapper.SysBalanceRecordMapper;
import org.jeecg.modules.system.mapper.SysStatisticsCostMapper;
import org.jeecg.modules.system.service.ISysBalanceRecordService;
import org.jeecg.modules.system.service.ISysStatisticsCostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * @Description: 底层共通业务API，提供其他独立模块调用
 * @Author: scott
 * @Date:2019-4-20 
 * @Version:V1.0
 */
@Slf4j
@Service
public class SysStatisticsCostImpl extends ServiceImpl<SysStatisticsCostMapper, SysStatisticsCost> implements ISysStatisticsCostService {

	@Autowired
	private CommonAPI commonAPI;

	@Override
	public Result<IPage<SysStatisticsCost>> queryPageList(HttpServletRequest req, QueryWrapper<SysStatisticsCost> queryWrapper, Integer pageSize, Integer pageNo) {
		String username = JwtUtil.getUsername(TokenUtils.getTokenByRequest());
		Set<String> roles = commonAPI.queryUserRoles(username);
		if (!(roles != null && roles.contains("admin"))) {
			queryWrapper.eq("user_name", username);
		}
		Result<IPage<SysStatisticsCost>> result = new Result<IPage<SysStatisticsCost>>();
		//TODO 外部模拟登陆临时账号，列表不显示
		Page<SysStatisticsCost> page = new Page<SysStatisticsCost>(pageNo, pageSize);
		IPage<SysStatisticsCost> pageList = this.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		if (!pageList.getRecords().isEmpty()) {
			pageList.getRecords().forEach(item -> {item.setTemplateCode(item.getTemplateName()+"("+item.getTemplateCode()+")");});
		}
		//log.info(pageList.toString());
		return result;
	}
}