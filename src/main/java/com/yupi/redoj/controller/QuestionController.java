package com.yupi.redoj.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.redoj.annotation.AuthCheck;
import com.yupi.redoj.common.BaseResponse;
import com.yupi.redoj.common.DeleteRequest;
import com.yupi.redoj.common.ErrorCode;
import com.yupi.redoj.common.ResultUtils;
import com.yupi.redoj.constant.UserConstant;
import com.yupi.redoj.exception.BusinessException;
import com.yupi.redoj.exception.ThrowUtils;
import com.yupi.redoj.model.dto.question.QuestionAddRequest;
import com.yupi.redoj.model.dto.question.QuestionEditRequest;
import com.yupi.redoj.model.dto.question.QuestionQueryRequest;
import com.yupi.redoj.model.dto.question.QuestionUpdateRequest;
import com.yupi.redoj.model.entity.Question;
import com.yupi.redoj.model.entity.User;
import com.yupi.redoj.model.vo.QuestionVO;
import com.yupi.redoj.service.QuestionService;
import com.yupi.redoj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题目接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/quest")
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService questService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建
     *
     * @param questAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questAddRequest, HttpServletRequest request) {
        if (questAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question quest = new Question();
        BeanUtils.copyProperties(questAddRequest, quest);
        List<String> tags = questAddRequest.getTags();
        if (tags != null) {
            quest.setTags(JSONUtil.toJsonStr(tags));
        }
        questService.validQuestion(quest, true);
        User loginUser = userService.getLoginUser(request);
        quest.setUserId(loginUser.getId());
        quest.setFavourNum(0);
        quest.setThumbNum(0);
        boolean result = questService.save(quest);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newQuestionId = quest.getId();
        return ResultUtils.success(newQuestionId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Question oldQuestion = questService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestion.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = questService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param questUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questUpdateRequest) {
        if (questUpdateRequest == null || questUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question quest = new Question();
        BeanUtils.copyProperties(questUpdateRequest, quest);
        List<String> tags = questUpdateRequest.getTags();
        if (tags != null) {
            quest.setTags(JSONUtil.toJsonStr(tags));
        }
        // 参数校验
        questService.validQuestion(quest, false);
        long id = questUpdateRequest.getId();
        // 判断是否存在
        Question oldQuestion = questService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = questService.updateById(quest);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question quest = questService.getById(id);
        if (quest == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(questService.getQuestionVO(quest, request));
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param questQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questQueryRequest) {
        long current = questQueryRequest.getCurrent();
        long size = questQueryRequest.getPageSize();
        Page<Question> questPage = questService.page(new Page<>(current, size),
                questService.getQueryWrapper(questQueryRequest));
        return ResultUtils.success(questPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param questQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questQueryRequest,
            HttpServletRequest request) {
        long current = questQueryRequest.getCurrent();
        long size = questQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> questPage = questService.page(new Page<>(current, size),
                questService.getQueryWrapper(questQueryRequest));
        return ResultUtils.success(questService.getQuestionVOPage(questPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param questQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questQueryRequest,
            HttpServletRequest request) {
        if (questQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        questQueryRequest.setUserId(loginUser.getId());
        long current = questQueryRequest.getCurrent();
        long size = questQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> questPage = questService.page(new Page<>(current, size),
                questService.getQueryWrapper(questQueryRequest));
        return ResultUtils.success(questService.getQuestionVOPage(questPage, request));
    }

    // endregion


    /**
     * 编辑（用户）
     *
     * @param questEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questEditRequest, HttpServletRequest request) {
        if (questEditRequest == null || questEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question quest = new Question();
        BeanUtils.copyProperties(questEditRequest, quest);
        List<String> tags = questEditRequest.getTags();
        if (tags != null) {
            quest.setTags(JSONUtil.toJsonStr(tags));
        }
        // 参数校验
        questService.validQuestion(quest, false);
        User loginUser = userService.getLoginUser(request);
        long id = questEditRequest.getId();
        // 判断是否存在
        Question oldQuestion = questService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = questService.updateById(quest);
        return ResultUtils.success(result);
    }

}
