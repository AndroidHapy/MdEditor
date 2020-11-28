/*
 * Copyright 2016. SHENQINCI(沈钦赐)<946736079@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blxt.mdedit;

import com.blxt.mdedit.base.BaseApplication;
import com.blxt.quicklog.crash.CrashHandler;

/**
 * Created by 沈钦赐 on 16/1/26.
 */
public class AppContext extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        CrashHandler.getInstance(this);//替换默认对象为当前对象

    }

    @Override
    protected boolean hasMemoryLeak() {
        return BuildConfig.DEBUG;
//        return false;
    }

    @Override
    protected boolean hasCrashLog() {
        return BuildConfig.DEBUG;
//        return false;
    }

}
