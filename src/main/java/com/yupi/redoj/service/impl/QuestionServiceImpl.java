package com.yupi.redoj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.redoj.model.entity.Question;
import com.yupi.redoj.service.QuestionService;
import com.yupi.redoj.mapper.QuestionMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【question(题目)】的数据库操作Service实现
* @createDate 2024-09-27 17:17:36
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
    implements QuestionService{

}




