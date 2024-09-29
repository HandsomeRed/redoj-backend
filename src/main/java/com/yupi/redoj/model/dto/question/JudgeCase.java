package com.yupi.redoj.model.dto.question;

import lombok.Data;

/**
 * 题目用例
 * @author TinyRed
 * @date 2024-09-30
 */
@Data
public class JudgeCase {

    /**
     * 输入用例
     */
    private String input;

    /**
     * 输出用例
     */
    private String output;
}
