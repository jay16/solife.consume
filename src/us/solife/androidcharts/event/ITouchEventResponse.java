package us.solife.androidcharts.event;
/*
 * ITouchEventResponse.java
 * Android-Charts
 *
 * Created by limc on 2011/05/29.
 *
 * Copyright 2011 limc.cn All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import us.solife.androidcharts.view.GridChart;

/**
 * 
 * <p>Interface for chart which is support response notify from other object</p>
 * <p>タッチイベント宥岑はレスピンズのオブジェクトのインタフェ�`ス</p>
 * <p>屶隔�贄κ村���連議緩窃斤�鷭喊�</p>
 *
 * @author limc 
 * @version v1.0 2013/05/30 17:57:32 
 * @see ITouchEventNotify
 */
public interface ITouchEventResponse {
	
	/**
	 * <p>Response Notify </p>
	 * <p>レスポンスをする</p>
	 * <p>�贄ν�岑</p>
	 * @param chart
	 * <p>source chart</p>
	 * <p>ソ�`スチャ�`ト</p>
	 * <p>坿遊斤��</p>
	 */
	public void notifyEvent(GridChart chart);
}
