package com.yupi.redoj.model.dto.question;

import lombok.Data;

/**
 * 题目配置
 * @author TinyRed
 * @date 2024-09-30
 */
@Data
public class JudgeConfig {

    /**
     * 时间限制（ms）
     */
    private Long timeLimit;

    /**
     * 内存限制（KB）
     */
    private Long memoryLimit;

    /**
     * 堆栈限制（KB）
     */
    private Long stackLimit;
}
