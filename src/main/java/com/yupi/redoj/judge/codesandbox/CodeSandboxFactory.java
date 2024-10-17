package com.yupi.redoj.judge.codesandbox;

import com.yupi.redoj.judge.codesandbox.impl.ExampleCodeSandbox;
import com.yupi.redoj.judge.codesandbox.impl.RemoteCodeSandbox;
import com.yupi.redoj.judge.codesandbox.impl.ThirdPartyCodeSandbox;

/**
 * 代码沙箱工厂（根据字符串参数创建指定的代码沙箱实例）
 */
public class CodeSandboxFactory {

    /**
     * 创建代码沙箱示例
     *
     * @param type 沙箱类型
     * @return 返回值CodeSandbox 代码沙箱接口，不是具体的实例，最终返回实现这个接口的实例
     */
    public static CodeSandbox newInstance(String type) {
        switch (type) {
            case "example":
                return new ExampleCodeSandbox();
            case "remote":
                return new RemoteCodeSandbox();
            case "thirdParty":
                return new ThirdPartyCodeSandbox();
            default:
                return new ExampleCodeSandbox();
        }
    }


}
