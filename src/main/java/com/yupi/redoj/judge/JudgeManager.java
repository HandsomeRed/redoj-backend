package com.yupi.redoj.judge;

import com.yupi.redoj.judge.strategy.DefaultJudgeStrategy;
import com.yupi.redoj.judge.strategy.JavaLanguageJudgeStrategy;
import com.yupi.redoj.judge.strategy.JudgeContext;
import com.yupi.redoj.judge.strategy.JudgeStrategy;
import com.yupi.redoj.judge.codesandbox.model.JudgeInfo;
import com.yupi.redoj.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}
